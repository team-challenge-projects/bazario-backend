package org.cyberrealm.tech.bazario.backend.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.RegistrationRequest;
import org.cyberrealm.tech.bazario.backend.exception.custom.EntityNotFoundException;
import org.cyberrealm.tech.bazario.backend.exception.custom.RegistrationException;
import org.cyberrealm.tech.bazario.backend.mapper.UserMapper;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.model.enums.Role;
import org.cyberrealm.tech.bazario.backend.repository.UserRepository;
import org.cyberrealm.tech.bazario.backend.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Role DEFAULT_ROLE = Role.USER;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    @Value("${token.expiration.minutes:15}")
    private int expirationMinutes;

    @Transactional
    @Override
    public void register(RegistrationRequest requestDto)
            throws RegistrationException {
        User user = userMapper.toModel(requestDto);
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RegistrationException("User with email: " + user.getEmail()
                    + " already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(DEFAULT_ROLE);
        user.setCreatedAt(LocalDateTime.now());
        redisTemplate.opsForValue().set(user.getEmail(), user,
                Duration.ofMinutes(expirationMinutes));
        userMapper.toUserResponse(user);
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("User not found"));
    }
}
