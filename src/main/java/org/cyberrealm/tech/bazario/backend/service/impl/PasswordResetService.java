package org.cyberrealm.tech.bazario.backend.service.impl;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final EmailService emailService;

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
}
