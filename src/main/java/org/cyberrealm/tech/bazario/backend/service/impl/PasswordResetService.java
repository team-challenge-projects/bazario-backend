package org.cyberrealm.tech.bazario.backend.service.impl;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.ResetPassword;
import org.cyberrealm.tech.bazario.backend.exception.custom.ArgumentNotValidException;
import org.cyberrealm.tech.bazario.backend.exception.custom.PasswordResetException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final EmailService emailService;
    private final PasswordEncoder encoder;
    private final EncryptionUtils encryptionUtils;

    @Value("${password.reset.code.length:6}")
    private int codeLength;
    @Value("${password.reset.code.expiration.minutes:3}")
    private int expirationMinutes;
    @Value("${frontend.reset.password.url}")
    private String frontendResetPasswordUrl;

    public void generatePasswordResetCode(String email) {
        String code = generateRandomCode(codeLength);
        String hashedCode = encoder.encode(code);
        redisTemplate.opsForValue().set(email, hashedCode, Duration.ofMinutes(expirationMinutes));
        String tokenPayload = email + ":" + code;

        String encryptedToken;
        try {
            encryptedToken = encryptionUtils.encrypt(tokenPayload);
        } catch (Exception e) {
            throw new PasswordResetException("Can't encrypt reset token");
        }
        String resetLink = frontendResetPasswordUrl
                + "reset token" + URLEncoder.encode(encryptedToken, StandardCharsets.UTF_8);
        emailService.sendPasswordResetEmail(email, "reset token",
                expirationMinutes, resetLink);
    }

    public boolean verifyPasswordResetToken(String token) {
        final String decryptedToken;
        try {
            decryptedToken = encryptionUtils.decrypt(token);
        } catch (Exception e) {
            //logger.error("Error decrypting reset token", e);
            return false;
        }
        String[] parts = decryptedToken.split(":");
        if (parts.length != 2) {
            //logger.warn("Invalid reset token format");
            return false;
        }
        String email = parts[0];
        String code = parts[1];

        String storedHashedCode = (String) redisTemplate.opsForValue().get(email);
        return storedHashedCode != null && encoder.matches(code, storedHashedCode);
    }

    public void removePasswordResetCode(String email) {
        redisTemplate.delete(email);
    }

    public void changePassword(ResetPassword resetPassword) {
        if (!isNotNullOrBlankAllArgument(resetPassword)) {
            throw new ArgumentNotValidException("Dto arguments is not null or blank");
        }

        String storedCode = (String) redisTemplate.opsForValue()
                .get(resetPassword.getEmail().get());
        String rawHex = resetPassword.getEmail().get() + storedCode;

        if (!encoder.matches(rawHex, resetPassword.getHex())) {
            throw new ArgumentNotValidException("Entered arguments is not valid");
        }
    }

    private static boolean isNotNullOrBlankAllArgument(ResetPassword resetPassword) {
        return resetPassword.getEmail().isPresent()
                && resetPassword.getEmail().get().isBlank()
                && resetPassword.getPassword().isPresent()
                && resetPassword.getPassword().get().isBlank()
                && resetPassword.getHex().isBlank();
    }

    private String generateRandomCode(int length) {
        String uuid = UUID.randomUUID().toString().replace(":", "");
        return uuid.substring(0, Math.min(length, uuid.length()));
    }
}
