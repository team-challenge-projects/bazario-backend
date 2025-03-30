package org.cyberrealm.tech.bazario.backend.service.impl;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.ResetPassword;
import org.cyberrealm.tech.bazario.backend.exception.custom.ArgumentNotValidExeption;
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

    @Value("${password.reset.code.length:6}")
    private int codeLength;

    @Value("${password.reset.code.expiration.minutes:3}")
    private int expirationMinutes;

    @Value("${frontend.reset.password.url}")
    private String frontendResetPasswordUrl;

    public void generatePasswordResetCode(String email) {
        String code = generateRandomCode(codeLength);
        redisTemplate.opsForValue().set(email, code, Duration.ofMinutes(expirationMinutes));

        String resetLink = frontendResetPasswordUrl
                + "?email=" + URLEncoder.encode(email, StandardCharsets.UTF_8)
                + "&code=" + URLEncoder.encode(code, StandardCharsets.UTF_8);

        emailService.sendPasswordResetEmail(email, "Password Reset",
                expirationMinutes, resetLink);
    }

    private String generateRandomCode(int length) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid.substring(0, Math.min(length, uuid.length()));
    }

    public boolean verifyPasswordResetCode(String email, String code) {
        String storedCode = (String) redisTemplate.opsForValue().get(email);
        return storedCode != null && storedCode.equals(code);
    }

    public void removePasswordResetCode(String email) {
        redisTemplate.delete(email);
    }

    public void changePassword(ResetPassword resetPassword) {
        if (!isNotNullOrBlankAllArgument(resetPassword)) {
            throw new ArgumentNotValidExeption("Dto arguments is not null or blank");
        }

        String storedCode = (String) redisTemplate.opsForValue()
                .get(resetPassword.getEmail().get());
        String rawHex = resetPassword.getEmail().get() + storedCode;

        if (!encoder.matches(rawHex, resetPassword.getHex())) {
            throw new ArgumentNotValidExeption("Entered arguments is not valid");
        }
    }

    private static boolean isNotNullOrBlankAllArgument(ResetPassword resetPassword) {
        return resetPassword.getEmail().isPresent() &&
                resetPassword.getEmail().get().isBlank() &&
                resetPassword.getPassword().isPresent() &&
                resetPassword.getPassword().get().isBlank() &&
                resetPassword.getHex().isBlank();
    }
}
