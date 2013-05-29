package ssotest.web;

import static ssotest.service.UrlEncoder.encode;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;

@Controller
public class GoogleOAuthController {
    private static Logger logger = LoggerFactory.getLogger(GoogleOAuthController.class);
    private static ObjectMapper objectMapper = new ObjectMapper();
    
    @Value("${google.oauth.client_id}")
    private String clientId;
    @Value("${google.oauth.client_secret}")
    private String clientSecret;
    @Value("${google.oauth.return_url}")
    private String returnUrl;
    
    private HttpClient httpClient = new DefaultHttpClient();
    private JsonFactory jsonFactory = new JacksonFactory();
    private GoogleIdTokenVerifier googleIdTokenVerifier;
    
    public GoogleOAuthController() {
        googleIdTokenVerifier = new GoogleIdTokenVerifier(new ApacheHttpTransport(httpClient), jsonFactory);
    }
    
    @RequestMapping(value="/auth/google/framed")
    public String framed() {
        return "oauth/google/iframe";
    }
            
    @RequestMapping(value="/auth/google/start")
    public String start(HttpServletRequest request) {
        String stateToken = new BigInteger(130, new SecureRandom()).toString();
        logger.debug("Storing CSRF token to session: " + stateToken);
        request.getSession().setAttribute("stateToken", stateToken);
        return String.format("redirect:https://accounts.google.com/o/oauth2/auth?client_id=%s&response_type=code&scope=openid%%20email%%20profile&redirect_uri=%s&state=token%%3A%s", clientId, encode(returnUrl), stateToken);
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value="/auth/google/return")
    public String callback(Model model, HttpServletRequest request) {
        String tokenInSession = (String) request.getSession().getAttribute("stateToken");
        logger.debug("CSRF token from session: " + tokenInSession);
        String stateParameter = request.getParameter("state");
        if (!StringUtils.equals("token:" + tokenInSession, stateParameter)) {
            logger.error("State value doesn't match token in session:" + stateParameter);
            throw new UnauthorizedRequestException();
        }
        
        
        try {
            HttpPost tokenRequest = new HttpPost("https://accounts.google.com/o/oauth2/token");
            tokenRequest.setEntity(new UrlEncodedFormEntity(Arrays.asList(
                    new BasicNameValuePair("code", request.getParameter("code")),
                    new BasicNameValuePair("client_id", clientId),
                    new BasicNameValuePair("client_secret", clientSecret),
                    new BasicNameValuePair("redirect_uri", returnUrl),
                    new BasicNameValuePair("grant_type", "authorization_code"))));
            
            HttpResponse response = httpClient.execute(tokenRequest);
            String responseContent = IOUtils.toString(response.getEntity().getContent());
            logger.debug("Received response: " + responseContent);
            tokenRequest.abort();
            
            Map<String, Object> authTokenResponseData = objectMapper.readValue(responseContent, Map.class);
            String base64IdToken = (String) authTokenResponseData.get("id_token");
            
            GoogleIdToken idToken = GoogleIdToken.parse(jsonFactory, base64IdToken);
            boolean result = idToken.verify(googleIdTokenVerifier);
            if (result) {
                logger.debug("ID token verification successful.");
                Payload tokenPayload = idToken.getPayload();
                logger.debug("User email: " + tokenPayload.getEmail());
                model.addAttribute("userEmail", tokenPayload.getEmail());
                
                String accessToken = encode(authTokenResponseData.get("access_token").toString());
                HttpGet userInfoRequest = new HttpGet("https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + accessToken);
                HttpResponse userInfoResponse = httpClient.execute(userInfoRequest);
                String userInfoJson = IOUtils.toString(userInfoResponse.getEntity().getContent());
                logger.debug("User Info: " + userInfoJson);
                
                Map<String, Object> userInfo = objectMapper.readValue(userInfoJson, Map.class);
                model.addAttribute("userName", userInfo.get("name"));
                
                
                return "oauth/google/authenticated";
            }
            
            throw new UnauthorizedRequestException();
        } catch (Exception e) {
            logger.error("Token request failed.", e);
            throw new RuntimeException("Token request failed.", e);
        }
    }
    
}
