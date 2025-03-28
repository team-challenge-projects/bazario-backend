package org.cyberrealm.tech.bazario.backend.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class CookieService {
    public HttpHeaders getCookieHeader(String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/public/refreshToken")
                .sameSite("Strict")
                .maxAge(7 * 24 * 60 * 60) // Время жизни куки в секундах
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        return headers;
    }

    public void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie clearCookie = new Cookie("refreshToken", null);
        clearCookie.setHttpOnly(true);
        clearCookie.setSecure(true); 
        clearCookie.setPath("/auth/refresh");
        clearCookie.setMaxAge(0);
        response.addCookie(clearCookie);
    }
}
