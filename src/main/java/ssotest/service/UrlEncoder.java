package ssotest.service;

import java.net.URLEncoder;

public class UrlEncoder {
    public static String encode(String input) {
        try {
            return URLEncoder.encode(input, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
