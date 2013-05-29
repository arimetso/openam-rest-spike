package ssotest.service;

import static ssotest.service.UrlEncoder.encode;

import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class OpenamAuthenticationProvider implements AuthenticationProvider {
    private static Logger logger = LoggerFactory.getLogger(OpenamAuthenticationProvider.class);
    private HttpClient httpClient;
    
    @Value("${openam.authentication.uri}")
    private String authenticationUri;
    @Value("${openam.host}")
    private String openAmHost;

    public OpenamAuthenticationProvider() {
        httpClient = new DefaultHttpClient();
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;

        String authenticationRealmAndService = encode(authenticationUri);
        String params = String.format("?username=%s&password=%s&uri=%s", encode(token.getPrincipal().toString()), encode(token.getCredentials().toString()), authenticationRealmAndService);
        HttpGet request = new HttpGet(String.format("http://%s/openam/identity/authenticate%s", openAmHost, params));
        try {
            HttpResponse response = httpClient.execute(request);
            String content = IOUtils.toString(response.getEntity().getContent());
            request.abort();
            logger.debug("Authentication response: " + content);
            
            if (StringUtils.indexOf(content, "InvalidCredentials Authentication Failed") > 0) {
                throw new BadCredentialsException("Authentication Failed");
            }
            
            return new UsernamePasswordAuthenticationToken(new UserToken(token.getPrincipal(), content), null, Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
        } catch (Exception e) {
            logger.error("Call to OpenAM failed", e);
            throw new AuthenticationServiceException("Call to OpenAM failed", e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
    
    public static class UserToken {
        private String token;
        private String principal;
        
        public UserToken(Object principal, String response) {
            if (StringUtils.startsWith(response, "token.id=")) {
                principal = principal.toString();
                token = StringUtils.substring(response, 9);
            } else {
                throw new BadCredentialsException("Authentication failed");
            }
        }

        public String getToken() {
            return token;
        }

        public String getPrincipal() {
            return principal;
        }

        @Override
        public String toString() {
            return String.format("UserToken:{principal=%s, token=%s}", principal, token);
        }
    }
}
