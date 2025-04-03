package org.cyberrealm.tech.bazario.backend.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.cyberrealm.tech.bazario.backend.api.UserApiDelegate;
import org.cyberrealm.tech.bazario.backend.dto.PatchUser;
import org.cyberrealm.tech.bazario.backend.dto.PrivateUserInformation;
import org.cyberrealm.tech.bazario.backend.dto.RegistrationRequest;
import org.cyberrealm.tech.bazario.backend.dto.UserInformation;
import org.cyberrealm.tech.bazario.backend.dto.VerificationEmail;
import org.cyberrealm.tech.bazario.backend.exception.RegistrationException;
import org.cyberrealm.tech.bazario.backend.mapper.UserMapper;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.model.enums.Role;
import org.cyberrealm.tech.bazario.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserApiDelegateImpl implements UserApiDelegate {
    private static final int ZERO = 0;
    private static final int ONE = 1;
    private static final int DEFAULT_LENGTH = 2;
    private static final String DELIMITER = ":";
    private static final String TARGET = "-";
    private static final String REPLACEMENT = "";
    private static final String SUBJECT = "Password Reset";
    private static final String TOKEN_PARAM = "?token=";
    private static final Logger logger = LoggerFactory.getLogger(UserApiDelegateImpl.class);
    private final RedisTemplate<String, Object> redisTemplate;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final EncryptionUtils encryptionUtils;
    private static final Role DEFAULT_ROLE = Role.ROLE_USER;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    @Value("${verify.email.code.expiration.hours:24}")
    private int expirationHours;

    @Transactional
    @Override
    public ResponseEntity<Void> createUser(RegistrationRequest registrationRequest) {
        User user = userMapper.toModel(registrationRequest);
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RegistrationException("User with email: " + user.getEmail()
                    + " already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(DEFAULT_ROLE);
        user.setCreatedAt(LocalDateTime.now());
        redisTemplate.opsForValue().set(user.getEmail(),user, Duration.ofHours(expirationHours));
        emailService.sendVerificationEmail(user.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<Void> deleteUser() {
        return UserApiDelegate.super.deleteUser();
    }

    @Override
    public ResponseEntity<UserInformation> getOtherUserInformation(Long id) {
        return UserApiDelegate.super.getOtherUserInformation(id);
    }

    @Override
    public ResponseEntity<PrivateUserInformation> getUserInformation() {
        return UserApiDelegate.super.getUserInformation();
    }

    @Override
    public ResponseEntity<Void> sendMessage(String type, String body) {
        return UserApiDelegate.super.sendMessage(type, body);
    }

    @Override
    public ResponseEntity<UserInformation> updateUser(PatchUser patchUser) {
        return UserApiDelegate.super.updateUser(patchUser);
    }

    @Override
    public ResponseEntity<UserInformation> updateUserByAdmin(Long id, PatchUser patchUser) {
        return UserApiDelegate.super.updateUserByAdmin(id, patchUser);
    }

    @Override
    public ResponseEntity<String> verifyEmail(String type, VerificationEmail verificationEmail) {
        return UserApiDelegate.super.verifyEmail(type, verificationEmail);
    }
}
