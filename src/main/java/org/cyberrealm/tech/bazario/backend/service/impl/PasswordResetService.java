package org.cyberrealm.tech.bazario.backend.service.impl;

import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final EmailService emailService;

    public void generatePasswordResetCode(String email) {
        String code = UUID.randomUUID().toString().substring(0, 6);
        redisTemplate.opsForValue().set(email, code, Duration.ofMinutes(3));
        String subject = "Password Reset Code";
        String text = "Your password reset code is: " + code + "\nIt is valid for 3 minutes";
        emailService.sendEmail(email, subject, text);
    }

    public boolean verifyPasswordResetCode(String email, String code) {
        String storedCode = (String) redisTemplate.opsForValue().get(email);
        return storedCode != null && storedCode.equals(code);
    }

    public void removePasswordResetCode(String email) {
        redisTemplate.delete(email);
    }
}
