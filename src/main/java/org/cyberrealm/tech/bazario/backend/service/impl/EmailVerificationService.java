package org.cyberrealm.tech.bazario.backend.service.impl;

import static org.cyberrealm.tech.bazario.backend.model.enums.MessageType.EMAIL_VERIFICATION;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.RegistrationRequest;
import org.cyberrealm.tech.bazario.backend.dto.VerificationEmail;
import org.cyberrealm.tech.bazario.backend.exception.custom.EntityNotFoundException;
import org.cyberrealm.tech.bazario.backend.exception.custom.RegistrationException;
import org.cyberrealm.tech.bazario.backend.mapper.UserMapper;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.model.enums.Role;
import org.cyberrealm.tech.bazario.backend.repository.UserRepository;
import org.cyberrealm.tech.bazario.backend.service.TokenService;
import org.cyberrealm.tech.bazario.backend.service.VerificationService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailVerificationService implements VerificationService {
    private static final Role DEFAULT_ROLE = Role.USER;
    private static final int INDEX_OLD_EMAIL = 0;
    private static final int INDEX_NEW_EMAIL = 1;

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final TokenService tokenService;
    private final ObjectMapper mapper;
    private final PasswordEncoder passwordEncoder;

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
        var dtoJson = (String) redisTemplate.opsForValue().getAndDelete(email
                + EMAIL_VERIFICATION_KEY_SUFFIX);

        var dtoEmail = (String) redisTemplate.opsForValue().getAndDelete(email
                + CHANGE_EMAIL_DTO_SUFFIX);

        if (dtoJson != null) {
            try {
                var dto = mapper.readValue(dtoJson, RegistrationRequest.class);

                User user = userMapper.toModel(dto);

                user.setPassword(passwordEncoder.encode(user.getPassword()));
                user.setRole(DEFAULT_ROLE);
                user.setCreatedAt(LocalDateTime.now());
                userRepository.save(user);
            } catch (JsonProcessingException e) {
                throw new RegistrationException("Not read json dto by user with email %s"
                        .formatted(email));
            }

        } else if (dtoEmail != null) {
            var emails = dtoEmail.split(":");
            User user = userRepository.findByEmail(emails[INDEX_OLD_EMAIL]).orElseThrow(() ->
                    new EntityNotFoundException("User with email %s not found"
                            .formatted(email)));
            user.setEmail(emails[INDEX_NEW_EMAIL]);
            userRepository.save(user);
        } else {
            throw new RegistrationException("User with email %s not registration"
                    .formatted(email));
        }

    }
}
