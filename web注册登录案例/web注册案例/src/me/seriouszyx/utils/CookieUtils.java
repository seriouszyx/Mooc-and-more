package me.seriouszyx.utils;

import javax.servlet.http.Cookie;

/** Cookie查找的工具类 */
public class CookieUtils {
    public static Cookie findCookie(Cookie[] cookies, String name) {
        if (cookies == null) {
            // 说明客户端没有携带 cookie
            return null;
        } else {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie;
                }
            }
            return null;
        }
    }
}
