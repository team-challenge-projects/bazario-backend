package org.cyberrealm.tech.bazario.backend.security;

import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.api.AuthenticationApiDelegate;
import org.cyberrealm.tech.bazario.backend.dto.AuthenticationRequest;
import org.cyberrealm.tech.bazario.backend.exception.AuthenticationProcessingException;
import org.cyberrealm.tech.bazario.backend.model.RefreshToken;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.repository.RefreshTokenRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationApiDelegateImpl implements AuthenticationApiDelegate {
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final CookieService cookieService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public ResponseEntity<String> login(AuthenticationRequest request) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        String accessToken = jwtUtil.generateAccessToken(authentication.getName());
        String refreshToken = jwtUtil.generateRefreshToken(authentication.getName());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieService.getRefreshCookie(refreshToken))
                .body(accessToken);
    }

    @Override
    public ResponseEntity<String> refreshToken(String refreshToken) {
        if (jwtUtil.isValidToken(refreshToken)) {
            String username = jwtUtil.getUsername(refreshToken);
            return ResponseEntity.ok(jwtUtil.generateAccessToken(username));
        }
        throw new AuthenticationProcessingException("Invalid refresh token");
    }
}
