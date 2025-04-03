//package org.cyberrealm.tech.bazario.backend.security;
//
//import io.jsonwebtoken.JwtException;
//import lombok.RequiredArgsConstructor;
//import org.cyberrealm.tech.bazario.backend.dto.AccessTokenResponseDto;
//import org.cyberrealm.tech.bazario.backend.dto.UserLoginRequestDto;
//import org.cyberrealm.tech.bazario.backend.dto.UserLoginResponseDto;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class AuthenticationService {
//    private final JwtUtil jwtUtil;
//    private final AuthenticationManager authenticationManager;
//
//    public UserLoginResponseDto authenticate(UserLoginRequestDto request) {
//        final Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(request.email(), request.password())
//        );
//        String accessToken = jwtUtil.generateAccessToken(authentication.getName());
//        String refreshToken = jwtUtil.generateRefreshToken(authentication.getName());
//        return new UserLoginResponseDto(accessToken, refreshToken);
//    }
//
//    public AccessTokenResponseDto refreshAccessToken(String refreshToken) {
//        if (jwtUtil.isValidToken(refreshToken)) {
//            String username = jwtUtil.getUsername(refreshToken);
//            return new AccessTokenResponseDto(jwtUtil.generateAccessToken(username));
//        }
//        throw new JwtException("Invalid refresh token");
//    }
//}
