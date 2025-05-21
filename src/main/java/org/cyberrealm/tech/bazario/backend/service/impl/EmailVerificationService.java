package org.cyberrealm.tech.bazario.backend.service.impl;

import static org.cyberrealm.tech.bazario.backend.model.enums.MessageType.EMAIL_VERIFICATION;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.VerificationEmail;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.repository.UserRepository;
import org.cyberrealm.tech.bazario.backend.service.TokenService;
import org.cyberrealm.tech.bazario.backend.service.VerificationService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailVerificationService implements VerificationService {
    private static final String EMAIL_VERIFICATION_KEY_SUFFIX = ":EMAIL_VERIFICATION";
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    public boolean verifyToken(VerificationEmail verificationEmail) {
        String email = verificationEmail.getEmail();
        String providedHex = verificationEmail.getHex();

        if (email == null || email.isBlank() || providedHex == null || providedHex.isBlank()) {
            return false;
        }
        String decodedHex = URLDecoder.decode(providedHex, StandardCharsets.UTF_8);

        return tokenService.verifyToken(decodedHex, email, EMAIL_VERIFICATION);
    }

    public void markVerified(String email) {
        User user = (User) redisTemplate.opsForValue().get(email);
        if (user != null) {
            userRepository.save(user);
        }
        String key = email + EMAIL_VERIFICATION_KEY_SUFFIX;
        redisTemplate.delete(key);
    }
}
