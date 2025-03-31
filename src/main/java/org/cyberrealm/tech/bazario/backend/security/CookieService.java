package org.cyberrealm.tech.bazario.backend.security;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import java.time.Duration;

@Service
public class CookieService {
    public HttpHeaders getCookieHeader(String refreshToken) {
        int maxAgeDays = 7;
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/public/refreshToken")
                .sameSite("Strict")
                .maxAge(Duration.ofDays(maxAgeDays))
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        return headers;
    }

    public HttpHeaders clearRefreshTokenCookie(String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/public/refreshToken")
                .maxAge(Duration.ZERO)
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        return headers;
    }
}
