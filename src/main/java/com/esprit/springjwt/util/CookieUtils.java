package com.esprit.springjwt.util;

import org.springframework.util.SerializationUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Optional;

public class CookieUtils {

    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return Optional.of(cookie);
                }
            }
        }

        return Optional.empty();
    }

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        // SameSite=None;Secure is required so the cookie survives the cross-site redirect
        // that happens when the OAuth2 provider (Google/GitHub) sends the user back.
        String header = name + "=" + value
                + "; Path=/"
                + "; HttpOnly"
                + "; Secure"
                + "; SameSite=None"
                + "; Max-Age=" + maxAge;
        response.addHeader("Set-Cookie", header);
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie: cookies) {
                if (cookie.getName().equals(name)) {
                    String header = name + "="
                            + "; Path=/"
                            + "; HttpOnly"
                            + "; Secure"
                            + "; SameSite=None"
                            + "; Max-Age=0";
                    response.addHeader("Set-Cookie", header);
                }
            }
        }
    }

    public static String serialize(Object object) {
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(object));
    }

    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(SerializationUtils.deserialize(
                        Base64.getUrlDecoder().decode(cookie.getValue())));
    }


}
