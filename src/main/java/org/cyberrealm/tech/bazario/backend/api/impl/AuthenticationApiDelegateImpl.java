package org.cyberrealm.tech.bazario.backend.api.impl;

import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.api.AuthenticationApiDelegate;
import org.cyberrealm.tech.bazario.backend.dto.AuthenticationRequest;
import org.cyberrealm.tech.bazario.backend.security.AuthenticationService;
import org.cyberrealm.tech.bazario.backend.security.CookieService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationApiDelegateImpl implements AuthenticationApiDelegate {
    private final AuthenticationService authenticationService;
    private final CookieService cookieService;

    @Override
    public ResponseEntity<String> login(AuthenticationRequest authenticationRequest) {
        var loginResponse = authenticationService.authenticate(authenticationRequest);
        return ResponseEntity.ok()
                .headers(cookieService.getCookieHeader(loginResponse.refreshToken()))
                .body(loginResponse.accessToken());
    }

    @Override
    public ResponseEntity<String> refreshToken(String refreshToken) {
        return ResponseEntity.ok(authenticationService.refreshAccessToken(refreshToken));
    }

    @Override
    public ResponseEntity<Void> logoutToken(String refreshToken) {
        authenticationService.clearRefreshTokenCookie(refreshToken);
        return ResponseEntity.noContent()
                .headers(cookieService.clearRefreshTokenCookie(refreshToken))
                .build();
    }
}
