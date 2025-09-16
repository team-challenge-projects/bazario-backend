package org.cyberrealm.tech.bazario.backend.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.AdStatus;
import org.cyberrealm.tech.bazario.backend.dto.PatchUser;
import org.cyberrealm.tech.bazario.backend.dto.PrivateUserInformation;
import org.cyberrealm.tech.bazario.backend.dto.PublicUserInformation;
import org.cyberrealm.tech.bazario.backend.dto.RegistrationRequest;
import org.cyberrealm.tech.bazario.backend.dto.UserInformation;
import org.cyberrealm.tech.bazario.backend.exception.custom.EntityNotFoundException;
import org.cyberrealm.tech.bazario.backend.exception.custom.ForbiddenException;
import org.cyberrealm.tech.bazario.backend.exception.custom.RegistrationException;
import org.cyberrealm.tech.bazario.backend.mapper.UserMapper;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.model.enums.Role;
import org.cyberrealm.tech.bazario.backend.repository.RefreshTokenRepository;
import org.cyberrealm.tech.bazario.backend.repository.UserRepository;
import org.cyberrealm.tech.bazario.backend.service.AdDeleteService;
import org.cyberrealm.tech.bazario.backend.service.AuthenticationUserService;
import org.cyberrealm.tech.bazario.backend.service.UserService;
import org.cyberrealm.tech.bazario.backend.service.VerificationService;
import org.cyberrealm.tech.bazario.backend.util.GeometryUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final int COUNT_MIN_ROOT_USER = 2;
    private static final String REGEX_DELIMITER_COORDINATE = "\\|";
    private static final int INDEX_FIRST_COORDINATE = 0;
    private static final int INDEX_TWO_COORDINATE = 1;
    private static final int DIVIDER_TO_KILOMETERS = 1000;

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthenticationUserService authService;
    private final AdDeleteService adDeleteService;
    private final RefreshTokenRepository tokenRepository;
    private final ObjectMapper mapper;

    @Value("${token.expiration.minutes:15}")
    private int expirationMinutes;
    @Value("${user.defaultCoordinate}")
    private String defaultCoordinate;

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
        User currentUser = userRepository.findByIdWithParameters(
                        authService.getCurrentUser().getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Current user not found"));
        var pointUser = Optional.ofNullable(user.getCityCoordinate()).orElseGet(() ->
                GeometryUtil.createPoint(defaultCoordinate));
        var pointCurrentUser = Optional.ofNullable(currentUser.getCityCoordinate()).orElseGet(() ->
                GeometryUtil.createPoint(defaultCoordinate));
        return userMapper.toPublicInformation(user,
                GeometryUtil.haversine(pointCurrentUser, pointUser));
    }

    @Override
    public PrivateUserInformation updateById(Long id, PatchUser patchUser) {
        User user = userRepository.findByIdWithParameters(id).orElseThrow(() ->
                new EntityNotFoundException("User not found"));
        checkUserRestrictions(patchUser, user);
        userMapper.updateUser(patchUser, user);
        if (patchUser.getCityCoordinate() != null) {
            user.setCityCoordinate(GeometryUtil.createPoint(
                    patchUser.getCityCoordinate()));
        }

        return userMapper.toInformation(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("User by id %d not found"
                        .formatted(id)));
        adDeleteService.deleteByUser(user);
        tokenRepository.deleteByUser(user);
        userRepository.delete(user);
    }

    @Override
    public void delete() {
        User currentUser = userRepository.findByIdWithParameters(
                        authService.getCurrentUser().getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found"));
        currentUser.setEmail(PREFIX_DELETE + currentUser.getEmail());
        currentUser.setParameters(Set.of());
        currentUser.setLocked(true);
        adDeleteService.changeStatusByUser(currentUser, AdStatus.DELETE);
        tokenRepository.deleteByUser(currentUser);
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
            if (Boolean.TRUE.equals(dto.getIsLocked())) {
                throw new ForbiddenException("ROOT user cannot locked");
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
                        user.getEmail() + DELIMITER_CHANGE_EMAIL + dto.getEmail(),
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

    @Override
    public PublicUserInformation getPublicInformationById(Long id) {
        var user = userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("User with id %d not found"
                        .formatted(id)));
        return userMapper.toInformationForAnonymous(user);
    }

    @Override
    public List<Long> getUserIdByDistance(double distance) {
        User currentUser = userRepository.findByIdWithParameters(
                        authService.getCurrentUser().getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "User not found"));

        return userRepository.findByDistance(currentUser.getCityCoordinate(),
                distance * DIVIDER_TO_KILOMETERS);
    }
}
