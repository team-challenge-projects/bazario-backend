package org.cyberrealm.tech.bazario.backend.api.impl;

import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.api.AuthenticationApiDelegate;
import org.cyberrealm.tech.bazario.backend.dto.AuthenticationRequest;
import org.cyberrealm.tech.bazario.backend.dto.RefreshTokenRequest;
import org.cyberrealm.tech.bazario.backend.security.AuthenticationService;
import org.cyberrealm.tech.bazario.backend.security.CookieService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationApiDelegateImpl implements AuthenticationApiDelegate {
    private final AuthenticationService authenticationService;
    private final CookieService cookieService;

    @Override
    public ResponseEntity<String> login(AuthenticationRequest authenticationRequest) {
        try {
            var loginResponse = authenticationService.authenticate(authenticationRequest);
            return ResponseEntity.ok()
                    .headers(cookieService.getCookieHeader(loginResponse.refreshToken()))
                    .body(loginResponse.accessToken());
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("""
                    { "error" : "Unauthorized", "message" : "Login or password is not valid."}
                    """);
        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("""
                    { "error" : "Forbidden", "message" : "Account deactivated"}
                    """);
        }
    }

    @Override
    public ResponseEntity<String> refreshToken(RefreshTokenRequest refreshToken) {
        return ResponseEntity.ok(authenticationService.refreshAccessToken(
                refreshToken.getRefreshToken()));
    }

    @Override
    public ResponseEntity<Void> logoutToken(String refreshToken) {
        authenticationService.clearRefreshTokenCookie(refreshToken);
        return ResponseEntity.noContent()
                .headers(cookieService.clearRefreshTokenCookie(refreshToken))
                .build();
    }
}
