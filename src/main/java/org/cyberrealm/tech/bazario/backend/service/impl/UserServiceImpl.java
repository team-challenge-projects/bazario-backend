package org.cyberrealm.tech.bazario.backend.service.impl;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.UserRegistrationRequestDto;
import org.cyberrealm.tech.bazario.backend.dto.UserResponseDto;
import org.cyberrealm.tech.bazario.backend.exception.custom.AuthenticationException;
import org.cyberrealm.tech.bazario.backend.exception.custom.EntityNotFoundException;
import org.cyberrealm.tech.bazario.backend.exception.custom.RegistrationException;
import org.cyberrealm.tech.bazario.backend.mapper.UserMapper;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.model.enums.Role;
import org.cyberrealm.tech.bazario.backend.repository.UserRepository;
import org.cyberrealm.tech.bazario.backend.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Role DEFAULT_ROLE = Role.USER;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        User user = userMapper.toModel(requestDto);
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RegistrationException("User with email: " + user.getEmail()
                    + " already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(DEFAULT_ROLE);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationException("User not authenticated");
        }
        return (User) authentication.getPrincipal();
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


