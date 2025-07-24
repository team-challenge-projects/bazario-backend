package org.cyberrealm.tech.bazario.backend.security;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class CookieService {
    @Value("${cookie.refresh.age}")
    private int maxAge;

    /**
     * Set cookie to heder of response
     *
     * @param refreshToken Generated a refresh token
     * @return heder
     */
    public HttpHeaders getCookieHeader(String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/public/refreshToken")
                .sameSite("Strict")
                .maxAge(maxAge)
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        return headers;
    }

    /**
     * Set cookies with duration is zero to heder of response
     *
     * @param refreshToken Current refresh token
     * @return heder
     */
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
