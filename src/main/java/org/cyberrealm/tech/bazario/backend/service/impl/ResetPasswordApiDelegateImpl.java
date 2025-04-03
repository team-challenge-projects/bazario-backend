package org.cyberrealm.tech.bazario.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.api.ResetPasswordApiDelegate;
import org.cyberrealm.tech.bazario.backend.dto.ResetPassword;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResetPasswordApiDelegateImpl implements ResetPasswordApiDelegate {
    private static final int ZERO = 0;
    private static final int ONE = 1;
    private static final int DEFAULT_LENGTH = 2;
    private static final String DELIMITER = ":";
    private static final String TARGET = "-";
    private static final String REPLACEMENT = "";
    private static final String SUBJECT = "Password Reset";
    private static final String TOKEN_PARAM = "?token=";
    private static final Logger logger = LoggerFactory.getLogger(ResetPasswordApiDelegateImpl.class);
    private final RedisTemplate<String, Object> redisTemplate;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final EncryptionUtils encryptionUtils;

    @Override
    public ResponseEntity<Void> resetPassword(ResetPassword resetPassword) {
        return ResetPasswordApiDelegate.super.resetPassword(resetPassword);
    }
}
