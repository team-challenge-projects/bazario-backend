package org.cyberrealm.tech.bazario.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Optional;
import java.util.stream.Stream;
import org.cyberrealm.tech.bazario.backend.dto.PatchUser;
import org.cyberrealm.tech.bazario.backend.dto.RegistrationRequest;
import org.cyberrealm.tech.bazario.backend.exception.custom.ForbiddenException;
import org.cyberrealm.tech.bazario.backend.mapper.UserMapper;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.model.enums.Role;
import org.cyberrealm.tech.bazario.backend.repository.RefreshTokenRepository;
import org.cyberrealm.tech.bazario.backend.repository.UserRepository;
import org.cyberrealm.tech.bazario.backend.service.impl.UserServiceImpl;
import org.cyberrealm.tech.bazario.backend.util.GeometryUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    public static final String EMAIL = "test@example.com";
    public static final long ONE_ID = 1L;
    public static final long TWO_ID = 2L;
    public static final String CITY_TEST = "TEST";
    public static final String NEXT_CITY = "NEXT_CITY";
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ValueOperations<String, Object> valueOperations;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private AuthenticationUserService authService;
    @Mock
    private AdDeleteService adDeleteService;
    @Mock
    private RefreshTokenRepository tokenRepository;
    @Mock
    private ObjectMapper mapper;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void register() throws JsonProcessingException {
        ReflectionTestUtils.setField(userService, "expirationMinutes", 15);
        var dto = new RegistrationRequest().email(EMAIL);
        when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
        when(mapper.writeValueAsString(dto)).thenReturn("{dto}");
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        userService.register(dto);

        verify(valueOperations).set(EMAIL + VerificationService
                        .EMAIL_VERIFICATION_KEY_SUFFIX, "{dto}",
                Duration.ofMinutes(15));
    }

    @Test
    void getInformation() {
        var currentUser = new User();
        currentUser.setId(TWO_ID);
        currentUser.setCityName(NEXT_CITY);
        when(authService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findByIdWithParameters(TWO_ID)).thenReturn(Optional.of(currentUser));

        userService.getInformation();

        verify(userMapper).toInformation(currentUser);
    }

    @Test
    void getInformationById() {
        var user = new User();
        user.setId(ONE_ID);
        user.setCityName(CITY_TEST);
        user.setCityCoordinate(GeometryUtil.createPoint("POINT(30.3125 50.27)"));
        when(userRepository.findById(ONE_ID)).thenReturn(Optional.of(user));
        var currentUser = new User();
        currentUser.setId(TWO_ID);
        currentUser.setCityName(NEXT_CITY);
        currentUser.setCityCoordinate(GeometryUtil.createPoint("POINT(24.0123 49.5017)"));
        when(authService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findByIdWithParameters(TWO_ID)).thenReturn(Optional.of(currentUser));

        userService.getInformationById(ONE_ID);

        verify(userMapper).toPublicInformation(user, 459.2388132067447);
    }

    @Test
    void updateChangeEmail() {
        ReflectionTestUtils.setField(userService, "expirationMinutes", 15);
        var currentUser = new User();
        currentUser.setId(TWO_ID);
        currentUser.setEmail("old@example.com");
        currentUser.setRole(Role.USER);
        when(authService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findByIdWithParameters(TWO_ID)).thenReturn(Optional.of(currentUser));
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(userRepository.save(currentUser)).thenReturn(currentUser);
        var dto = new PatchUser().email("new@example.com");

        userService.update(dto);

        verify(valueOperations).set("new@example.com" + VerificationService.CHANGE_EMAIL_DTO_SUFFIX,
                "old@example.com:new@example.com", Duration.ofMinutes(15L));
        verify(userMapper).updateUser(dto, currentUser);
        verify(userMapper).toInformation(currentUser);
    }

    @Test
    void updateSetCoordinate() {
        var currentUser = new User();
        currentUser.setId(TWO_ID);
        currentUser.setCityCoordinate(GeometryUtil.createPoint("POINT(50.27 30.3125)"));
        currentUser.setRole(Role.USER);
        when(authService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findByIdWithParameters(TWO_ID)).thenReturn(Optional.of(currentUser));
        when(userRepository.save(currentUser)).thenReturn(currentUser);
        var dto = new PatchUser().cityCoordinate("49.5017|24.0123").cityName("Test");

        userService.update(dto);

        verify(userMapper).updateUser(dto, currentUser);
        verify(userMapper).toInformation(currentUser);
    }

    @ParameterizedTest
    @MethodSource
    void updateFail(PatchUser dto, User user, Class<? extends Throwable> clazz, String message) {
        when(authService.getCurrentUser()).thenReturn(user);
        if (!user.getRole().equals(Role.ROOT) && dto.getRole() != null) {
            when(authService.isRoot()).thenReturn(false);
        }
        if (!user.getRole().equals(Role.ROOT) && Boolean.TRUE.equals(dto.getIsLocked())) {
            when(authService.isAdmin()).thenReturn(false);
        }
        when(userRepository.findByIdWithParameters(user.getId())).thenReturn(Optional.of(user));

        var exception = assertThrows(clazz, () -> userService.update(dto));
        assertEquals(message, exception.getMessage());
    }

    public static Stream<Arguments> updateFail() {
        return Stream.of(
                Arguments.of(new PatchUser().role(Role.USER.name()),
                       Optional.empty().orElseGet(() -> {
                           var user = new User();
                           user.setRole(Role.ROOT);
                           return user;
                       }),
                        ForbiddenException.class,
                        "There must be at least one user with the ROOT role."),
                Arguments.of(new PatchUser().isLocked(true),
                        Optional.empty().orElseGet(() -> {
                            var user = new User();
                            user.setRole(Role.ROOT);
                            return user;
                        }),
                        ForbiddenException.class,
                        "ROOT user cannot locked"),
                Arguments.of(new PatchUser().role(Role.ADMIN.name()),
                        Optional.empty().orElseGet(() -> {
                            var user = new User();
                            user.setRole(Role.USER);
                            return user;
                        }),
                        ForbiddenException.class,
                        "Only a user with the ROOT role can change the role."),
                Arguments.of(new PatchUser().isLocked(true),
                        Optional.empty().orElseGet(() -> {
                            var user = new User();
                            user.setRole(Role.USER);
                            return user;
                        }),
                        ForbiddenException.class,
                        "Only a user with the ROOT or ADMIN role can locked user")
        );
    }

    @Test
    void updateById() {
    }

    @Test
    void delete() {
    }

    @Test
    void deleteById() {
    }

    @Test
    void getPublicInformationById() {
    }

    @Test
    void getUserIdByDistance() {
    }
}
