package org.cyberrealm.tech.bazario.backend.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.AdStatus;
import org.cyberrealm.tech.bazario.backend.dto.PatchUser;
import org.cyberrealm.tech.bazario.backend.dto.PrivateUserInformation;
import org.cyberrealm.tech.bazario.backend.dto.RegistrationRequest;
import org.cyberrealm.tech.bazario.backend.dto.UserInformation;
import org.cyberrealm.tech.bazario.backend.exception.custom.EntityNotFoundException;
import org.cyberrealm.tech.bazario.backend.exception.custom.ForbiddenException;
import org.cyberrealm.tech.bazario.backend.exception.custom.RegistrationException;
import org.cyberrealm.tech.bazario.backend.mapper.UserMapper;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.model.enums.Role;
import org.cyberrealm.tech.bazario.backend.repository.UserRepository;
import org.cyberrealm.tech.bazario.backend.service.AdDeleteService;
import org.cyberrealm.tech.bazario.backend.service.AuthenticationUserService;
import org.cyberrealm.tech.bazario.backend.service.UserService;
import org.cyberrealm.tech.bazario.backend.service.VerificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final int COUNT_MIN_ROOT_USER = 2;

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthenticationUserService authService;
    private final AdDeleteService adDeleteService;
    private final ObjectMapper mapper;

    @Value("${token.expiration.minutes:15}")
    private int expirationMinutes;

    @Transactional
    @Override
    public void register(RegistrationRequest requestDto)
            throws RegistrationException {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException("User with email: " + requestDto.getEmail()
                    + " already exists");
        }

        try {
            var jsonUser = mapper.writeValueAsString(requestDto);
            redisTemplate.opsForValue().set(requestDto.getEmail()
                            + VerificationService.EMAIL_VERIFICATION_KEY_SUFFIX,
                    jsonUser, Duration.ofMinutes(expirationMinutes));
        } catch (JsonProcessingException e) {
            throw new RegistrationException("Not convert request to json by user with email %s"
                    .formatted(requestDto.getEmail()));
        }
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
        checkUserRestrictions(patchUser, currentUser);
        userMapper.updateUser(patchUser, currentUser);
        if (patchUser.getEmail() != null && authService.isAdmin()) {
            currentUser.setEmail(patchUser.getEmail());
        }
        return userMapper.toInformation(userRepository.save(currentUser));
    }

    private void checkUserRestrictions(PatchUser dto, User user) {
        if (dto == null || user == null) {
            return;
        }
        if (user.getRole().equals(Role.ROOT)) {
            if (dto.getRole() != null && !dto.getRole().equals(user.getRole().name())
                    && userRepository.countByRole(Role.ROOT) < COUNT_MIN_ROOT_USER) {
                throw new ForbiddenException(
                        "There must be at least one user with the ROOT role.");
            }
        } else {
            if (dto.getRole() != null && !dto.getRole().equals(user.getRole().name())
                    && !authService.isRoot()) {
                throw new ForbiddenException(
                        "Only a user with the ROOT role can change the role.");
            }
            if (Boolean.TRUE.equals(dto.getIsLocked()) && !authService.isAdmin()) {
                throw new ForbiddenException(
                        "Only a user with the ROOT or ADMIN role can locked user");
            }

            if (dto.getEmail() != null && !dto.getEmail().equals(user.getEmail())) {
                redisTemplate.opsForValue().set(dto.getEmail()
                                + VerificationService.CHANGE_EMAIL_DTO_SUFFIX,
                        user.getEmail() + ":" + dto.getEmail(),
                        Duration.ofMinutes(expirationMinutes));
            }
        }

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
