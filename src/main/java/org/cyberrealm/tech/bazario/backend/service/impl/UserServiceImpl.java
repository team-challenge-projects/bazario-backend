package org.cyberrealm.tech.bazario.backend.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.AdStatus;
import org.cyberrealm.tech.bazario.backend.dto.PatchUser;
import org.cyberrealm.tech.bazario.backend.dto.PrivateUserInformation;
import org.cyberrealm.tech.bazario.backend.dto.RegistrationRequest;
import org.cyberrealm.tech.bazario.backend.dto.UserInformation;
import org.cyberrealm.tech.bazario.backend.exception.custom.EntityNotFoundException;
import org.cyberrealm.tech.bazario.backend.exception.custom.RegistrationException;
import org.cyberrealm.tech.bazario.backend.mapper.UserMapper;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.model.enums.Role;
import org.cyberrealm.tech.bazario.backend.repository.UserRepository;
import org.cyberrealm.tech.bazario.backend.service.AdDeleteService;
import org.cyberrealm.tech.bazario.backend.service.AuthenticationUserService;
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
    private final AuthenticationUserService authService;
    private final AdDeleteService adDeleteService;

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
    public UserInformation getInformationById(Long id) {
        var user = userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("User with id %d not found"
                        .formatted(id)));
        return userMapper.toPublicInformation(user);
    }

    @Override
    public PrivateUserInformation updateById(Long id, PatchUser patchUser) {
        User user = userRepository.findByIdWithParameters(id).orElseThrow(() ->
                new EntityNotFoundException("User not found"));
        userMapper.updateUser(patchUser, user);

        return userMapper.toInformation(userRepository.save(user));
    }

    @Override
    public void deleteById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("User by id %d not found"
                        .formatted(id)));
        adDeleteService.deleteByUser(user);
        userRepository.delete(user);
    }

    @Override
    public void delete() {
        User currentUser = userRepository.findByIdWithParameters(
                        authService.getCurrentUser().getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found"));
        currentUser.setEmail("delete_" + currentUser.getEmail());
        currentUser.setParameters(Set.of());
        currentUser.setLocked(true);
        adDeleteService.changeStatusByUser(currentUser, AdStatus.DELETE);
        userRepository.save(currentUser);
    }

    @Override
    public PrivateUserInformation update(PatchUser patchUser) {
        User currentUser = userRepository.findByIdWithParameters(
                        authService.getCurrentUser().getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found"));
        userMapper.updateUser(patchUser, currentUser);
        return userMapper.toInformation(userRepository.save(currentUser));
    }

    @Override
    public PrivateUserInformation getInformation() {
        User currentUser = userRepository.findByIdWithParameters(
                authService.getCurrentUser().getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found"));
        return userMapper.toInformation(currentUser);
    }
}
