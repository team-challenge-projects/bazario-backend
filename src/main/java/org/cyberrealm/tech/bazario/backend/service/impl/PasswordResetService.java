package org.cyberrealm.tech.bazario.backend.service.impl;

import static org.cyberrealm.tech.bazario.backend.model.enums.MessageType.PASSWORD_RESET;

import jakarta.transaction.Transactional;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.ResetPassword;
import org.cyberrealm.tech.bazario.backend.exception.custom.ArgumentNotValidException;
import org.cyberrealm.tech.bazario.backend.exception.custom.EntityNotFoundException;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.repository.UserRepository;
import org.cyberrealm.tech.bazario.backend.service.TokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void changePassword(ResetPassword resetPassword) {
        if (!isNotNullOrBlankAllArgument(resetPassword)) {
            throw new ArgumentNotValidException("Dto arguments is not null or blank");
        }
        String email = resetPassword.getEmail();
        String token = URLDecoder.decode(resetPassword.getHex(), StandardCharsets.UTF_8);

        boolean isValid = tokenService.verifyToken(token, email, PASSWORD_RESET);

        if (!isValid) {
            throw new ArgumentNotValidException("Entered arguments is not valid");
        }
        updatePassword(email, resetPassword.getPassword());
    }

    private static boolean isNotNullOrBlankAllArgument(ResetPassword resetPassword) {
        return resetPassword.getEmail() != null
                && !resetPassword.getEmail().isBlank()
                && resetPassword.getPassword() != null
                && !resetPassword.getPassword().isBlank()
                && resetPassword.getHex() != null
                && !resetPassword.getHex().isBlank();
    }

    public void updatePassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("User with email:%s not found", email))
                );

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

}
