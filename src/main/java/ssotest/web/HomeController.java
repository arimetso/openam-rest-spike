package ssotest.web;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {
    private static Logger logger = LoggerFactory.getLogger(HomeController.class);

    @RequestMapping(value = { "/", "/home" })
    public String home(HttpServletRequest request) {
        logRequestAndSessionInfo(request);
        return "home";
    }

    @SuppressWarnings("unchecked")
    public static void logRequestAndSessionInfo(HttpServletRequest request) {
        Enumeration<String> attributeNames = request.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String name = attributeNames.nextElement();
            logger.debug(String.format("Request Attribute '%s': %s", name, request.getAttribute(name).toString()));
        }
        HttpSession session = request.getSession();
        attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String name = attributeNames.nextElement();
            logger.debug(String.format("Session Attribute '%s': %s", name, session.getAttribute(name).toString()));
        }
        if (request.getUserPrincipal() != null) {
            logger.info(String.format("=== User: %s ===", request.getUserPrincipal().getName()));
        } else {
            logger.info("User principal is null");
        }
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return "login";
    }
}
