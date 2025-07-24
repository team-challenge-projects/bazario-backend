package org.cyberrealm.tech.bazario.backend.security;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.AuthenticationRequest;
import org.cyberrealm.tech.bazario.backend.dto.UserLoginResponseDto;
import org.cyberrealm.tech.bazario.backend.model.RefreshToken;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.repository.RefreshTokenRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository repository;

    /**
     *  Authentication of an anonymous user. Writing or rewriting
     *  a refresh token to the database.
     *
     * @param request Authentication form
     * @return Dto with access and refresh token
     */
    public UserLoginResponseDto authenticate(AuthenticationRequest request) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(),
                        request.getPassword())
        );
        String accessToken = jwtUtil.generateAccessToken(authentication.getName());
        String refreshToken = jwtUtil.generateRefreshToken(authentication.getName());
        User user = (User) authentication.getPrincipal();
        RefreshToken entity = repository.findByUserId(user.getId()).orElseGet(() ->
                RefreshToken.builder().user(user).build());
        entity.setToken(refreshToken);
        repository.save(entity);
        return new UserLoginResponseDto(accessToken, refreshToken);
    }

    /**
     * Generate an access token if the exists refresh token is valid.
     *
     * @param refreshToken Refresh token from request
     * @return New access token
     */
    public String refreshAccessToken(String refreshToken) {
        var entity = repository.findByToken(refreshToken);
        if (jwtUtil.isValidToken(refreshToken) && entity.isPresent()) {
            String username = jwtUtil.getUsername(refreshToken);
            return jwtUtil.generateAccessToken(username);
        }
        throw new JwtException("Invalid refresh token");
    }

    /**
     * Delete exists refresh token
     *
     * @param refreshToken Refresh token from request
     */
    public void clearRefreshTokenCookie(String refreshToken) {
        repository.deleteByToken(refreshToken);
    }
}
