package org.cyberrealm.tech.bazario.backend.service.impl;

import static org.cyberrealm.tech.bazario.backend.model.enums.MessageType.PASSWORD_RESET;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.ResetPassword;
import org.cyberrealm.tech.bazario.backend.exception.custom.ArgumentNotValidException;
import org.cyberrealm.tech.bazario.backend.service.TokenService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final TokenService tokenService;

    public void changePassword(ResetPassword resetPassword) {
        if (!isNotNullOrBlankAllArgument(resetPassword)) {
            throw new ArgumentNotValidException("Dto arguments is not null or blank");
        }
        String email = resetPassword.getEmail().get();
        String token = URLDecoder.decode(resetPassword.getHex(), StandardCharsets.UTF_8);

        boolean isValid = tokenService.verifyToken(token, email, PASSWORD_RESET);

        if (!isValid) {
            throw new ArgumentNotValidException("Entered arguments is not valid");
        }
    }

    private static boolean isNotNullOrBlankAllArgument(ResetPassword resetPassword) {
        return resetPassword.getEmail().isPresent()
                && !resetPassword.getEmail().get().isBlank()
                && resetPassword.getPassword().isPresent()
                && !resetPassword.getPassword().get().isBlank()
                && !resetPassword.getHex().isBlank();
    }
}
