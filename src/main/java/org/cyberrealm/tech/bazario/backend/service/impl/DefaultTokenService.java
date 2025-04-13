package org.cyberrealm.tech.bazario.backend.service.impl;

import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.model.enums.MessageType;
import org.cyberrealm.tech.bazario.backend.service.TokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultTokenService implements TokenService {
    private static final String DELIMITER = ":";
    private static final int DEFAULT_TOKEN_LENGTH = 2;
    private static final int EMAIL_INDEX = 0;
    private static final int CODE_INDEX = 1;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final EncryptionUtils encryptionUtils;

    @Value("${token.expiration.minutes:15}")
    private int expirationMinutes;
    @Value("${password.reset.code.length:6}")
    private int codeLength;

    @Override
    public String generateToken(String email, MessageType messageType) {
        String code = generateRandomCode(codeLength);
        String key = getKey(email, messageType);
        redisTemplate.opsForValue().set(key, passwordEncoder.encode(code),
                Duration.ofMinutes(expirationMinutes));

        String tokenPayload = email + DELIMITER + code;
        try {
            return encryptionUtils.encrypt(tokenPayload);
        } catch (Exception e) {
            throw new RuntimeException("Token encryption failed", e);
        }
    }

    @Override
    public boolean verifyToken(String token, String email, MessageType messageType) {
        String decryptedToken;
        try {
            decryptedToken = encryptionUtils.decrypt(token);
        } catch (Exception e) {
            return false;
        }
        String[] parts = decryptedToken.split(DELIMITER);
        if (parts.length != DEFAULT_TOKEN_LENGTH || !parts[EMAIL_INDEX].equals(email)) {
            return false;
        }
        String code = parts[CODE_INDEX];
        String key = getKey(email, messageType);
        String storedHash = (String) redisTemplate.opsForValue().get(key);
        return storedHash != null && passwordEncoder.matches(code, storedHash);
    }

    private String generateRandomCode(int length) {
        String uuid = UUID.randomUUID().toString();
        return uuid.substring(EMAIL_INDEX, Math.min(length, uuid.length()));
    }

    private String getKey(String email, MessageType messageType) {
        return email + DELIMITER + messageType.name();
    }
}
