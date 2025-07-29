package org.cyberrealm.tech.bazario.backend.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.model.RefreshToken;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.model.enums.Role;
import org.cyberrealm.tech.bazario.backend.repository.RefreshTokenRepository;
import org.cyberrealm.tech.bazario.backend.repository.UserRepository;
import org.cyberrealm.tech.bazario.backend.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * User authorization through a Google account and issuance of tokens.
 * If the user is not in the database, we write their data to the database.
 * A default password is created.
 */
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    private final RefreshTokenRepository tokenRepository;

    @Value("${cookie.refresh.age}")
    private int maxAge;
    @Value("${user.user.credentials.password}")
    private String password;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException {
        var user = (OAuth2User) authentication.getPrincipal();
        String email = user.getAttribute("email");
        User currentUser = repository.findByEmail(email).orElseGet(() -> {
            var newUser = new User();
            repository.findByEmail(UserService.PREFIX_DELETE + email)
                    .ifPresent(entity -> newUser.setId(entity.getId()));
            newUser.setEmail(email);
            newUser.setFirstName(user.getAttribute("given_name"));
            newUser.setLastName(user.getAttribute("family_name"));
            newUser.setAvatar(user.getAttribute("picture"));
            newUser.setRole(Role.USER);
            newUser.setPassword(passwordEncoder.encode(password));
            return repository.save(newUser);
        });

        String refreshToken = jwtUtil.generateRefreshToken(email);
        tokenRepository.save(RefreshToken.builder().token(refreshToken)
                .user(currentUser).build());
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/public/refreshToken");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);

        String accessToken = jwtUtil.generateAccessToken(email);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        PrintWriter writer = response.getWriter();
        writer.write(accessToken);
        writer.flush();
    }
}
