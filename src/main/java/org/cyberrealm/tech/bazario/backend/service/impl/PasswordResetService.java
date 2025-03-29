package org.cyberrealm.tech.bazario.backend.service.impl;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cyberrealm.tech.bazario.backend.exception.PasswordResetException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private static final int ZERO = 0;
    private static final int ONE = 1;
    private static final int DEFAULT_LENGTH = 2;
    private static final String DELIMITER = ":";
    private static final String TARGET = "-";
    private static final String REPLACEMENT = "";
    private static final String SUBJECT = "Password Reset";
    private static final String TOKEN_PARAM = "?token=";
    private static final Logger logger = LoggerFactory.getLogger(PasswordResetService.class);
    private final RedisTemplate<String, Object> redisTemplate;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final EncryptionUtils encryptionUtils;

    @Value("${password.reset.code.length:6}")
    private int codeLength;
    @Value("${password.reset.code.expiration.minutes:3}")
    private int expirationMinutes;
    @Value("${frontend.reset.password.url}")
    private String frontendResetPasswordUrl;

    public void generatePasswordResetCode(String email) {
        String code = generateRandomCode(codeLength);
        String hashedCode = passwordEncoder.encode(code);
        redisTemplate.opsForValue().set(email, hashedCode, Duration.ofMinutes(expirationMinutes));
        String tokenPayload = email + DELIMITER + code;

        String encryptedToken;
        try {
            encryptedToken = encryptionUtils.encrypt(tokenPayload);
        } catch (Exception e) {
            throw new PasswordResetException("Can't encrypt reset token");
        }
        String resetLink = frontendResetPasswordUrl
                + TOKEN_PARAM + URLEncoder.encode(encryptedToken, StandardCharsets.UTF_8);
        emailService.sendPasswordResetEmail(email, SUBJECT,
                expirationMinutes, resetLink);
    }

    public boolean verifyPasswordResetToken(String token) {
        final String decryptedToken;
        try {
            decryptedToken = encryptionUtils.decrypt(token);
        } catch (Exception e) {
            logger.error("Error decrypting reset token", e);
            return false;
        }
        String[] parts = decryptedToken.split(DELIMITER);
        if (parts.length != DEFAULT_LENGTH) {
            logger.warn("Invalid reset token format");
            return false;
        }
        String email = parts[ZERO];
        String code = parts[ONE];

        String storedHashedCode = (String) redisTemplate.opsForValue().get(email);
        return storedHashedCode != null && passwordEncoder.matches(code, storedHashedCode);
    }

    public void removePasswordResetCode(String email) {
        redisTemplate.delete(email);
    }

    private String generateRandomCode(int length) {
        String uuid = UUID.randomUUID().toString().replace(TARGET, REPLACEMENT);
        return uuid.substring(ZERO, Math.min(length, uuid.length()));
    }
}
