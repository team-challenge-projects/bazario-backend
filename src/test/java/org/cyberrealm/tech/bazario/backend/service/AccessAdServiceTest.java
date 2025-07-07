package org.cyberrealm.tech.bazario.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.cyberrealm.tech.bazario.backend.dto.AdStatus;
import org.cyberrealm.tech.bazario.backend.model.Ad;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.model.enums.Role;
import org.cyberrealm.tech.bazario.backend.repository.AdRepository;
import org.cyberrealm.tech.bazario.backend.service.impl.AccessAdServiceImpl;
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
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class AccessAdServiceTest {
    public static final long ONE_ID = 1L;
    @Mock
    private AdRepository adRepository;
    @Mock
    private AuthenticationUserService authUserService;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ZSetOperations<String, Object> opsForZSet;
    @Mock
    private ValueOperations<String, Object> opsForValue;
    @InjectMocks
    private AccessAdServiceImpl accessAdService;

    @Test
    void getProtectedAd() {
        var user = new User();
        user.setId(ONE_ID);
        user.setRole(Role.ADMIN);
        user.setLocked(false);
        var ad = new Ad();
        when(adRepository.findByIdWithParameters(ONE_ID)).thenReturn(Optional.of(ad));
        when(redisTemplate.opsForZSet()).thenReturn(opsForZSet);
        when(redisTemplate.opsForValue()).thenReturn(opsForValue);
        when(opsForZSet.addIfAbsent(anyString(),anyLong(), anyDouble())).thenReturn(true);
        when(opsForZSet.incrementScore(anyString(), anyLong(), anyDouble())).thenReturn(0.0);
        when(opsForValue.setIfAbsent(anyString(), anyLong(), any(Duration.class))).thenReturn(true);
        when(opsForZSet.size(anyString())).thenReturn(0L);
        when(authUserService.isAuthenticationUser()).thenReturn(true);
        when(authUserService.getCurrentUser()).thenReturn(user);

        assertEquals(ad, accessAdService.getProtectedAd(ONE_ID));
    }

    @Test
    void save() {
        var ad = new Ad();
        accessAdService.save(ad);
        verify(adRepository).save(ad);
    }

    @ParameterizedTest
    @MethodSource
    void isNotAccessAd(boolean isAuthUser, boolean isLocked,
                       Role role, AdStatus status, boolean expected) {
        var user = new User();
        user.setId(ONE_ID);
        user.setLocked(isLocked);
        user.setRole(role);
        var ad = new Ad();
        ad.setStatus(status);
        ad.setUser(user);
        when(authUserService.isAuthenticationUser()).thenReturn(isAuthUser);
        lenient().when(authUserService.getCurrentUser()).thenReturn(user);

        assertEquals(expected, accessAdService.isNotAccessAd(ad));
    }

    public static Stream<Arguments> isNotAccessAd() {
        return Stream.of(
                Arguments.of(false, false, Role.ROOT, AdStatus.DELETE, true),
                Arguments.of(true, true, Role.ROOT, AdStatus.DELETE, true),
                Arguments.of(true, false, Role.ROOT, AdStatus.DELETE, false),
                Arguments.of(true, false, Role.ADMIN, AdStatus.DELETE, false),
                Arguments.of(true, false, Role.USER, AdStatus.DELETE, true),
                Arguments.of(true, false, Role.USER, AdStatus.NEW, false),
                Arguments.of(true, false, Role.USER, AdStatus.ACTIVE, false),
                Arguments.of(true, false, Role.USER, AdStatus.DISABLE, false)
        );
    }

    @Test
    void getPublicAd() {
        var ad = new Ad();
        ad.setStatus(AdStatus.ACTIVE);
        when(adRepository.findByIdWithParameters(ONE_ID)).thenReturn(Optional.of(ad));
        when(redisTemplate.opsForZSet()).thenReturn(opsForZSet);
        when(redisTemplate.opsForValue()).thenReturn(opsForValue);
        when(opsForZSet.addIfAbsent(anyString(),anyLong(), anyDouble())).thenReturn(true);
        when(opsForZSet.incrementScore(anyString(), anyLong(), anyDouble())).thenReturn(0.0);
        when(opsForValue.setIfAbsent(anyString(), anyLong(), any(Duration.class))).thenReturn(true);
        when(opsForZSet.size(anyString())).thenReturn(0L);

        assertEquals(ad, accessAdService.getPublicAd(ONE_ID));
    }

    @ParameterizedTest
    @MethodSource("getLeaderBoardContent")
    void getLeaderBoardContentDesc(Map<String, String> filters,
                               Long size, Long page, Double min,
                               Double max) {
        when(redisTemplate.opsForZSet()).thenReturn(opsForZSet);
        var filter = new HashMap<>(filters);
        filter.put("sort", "desc");
        accessAdService.getLeaderBoardContent(filter);
        verify(opsForZSet).reverseRangeByScoreWithScores(
                "leaderBoard", min, max, size * page, size);
    }

    @ParameterizedTest
    @MethodSource("getLeaderBoardContent")
    void getLeaderBoardContentAsc(Map<String, String> filters,
                                   Long size, Long page, Double min,
                                   Double max) {
        when(redisTemplate.opsForZSet()).thenReturn(opsForZSet);
        var filter = new HashMap<>(filters);
        filter.put("sort", "asc");
        accessAdService.getLeaderBoardContent(filter);
        verify(opsForZSet).rangeByScoreWithScores(
                "leaderBoard", min, max, size * page, size);
    }

    public static Stream<Arguments> getLeaderBoardContent() {
        return Stream.of(
                Arguments.of(Map.of(), 16L, 0L, 0.0, -1.0),
                Arguments.of(Map.of("size", "5", "page", "2",
                        "min", "3", "max", "10"), 5L, 2L, 3.0, 10.0)
        );
    }

    @ParameterizedTest
    @MethodSource
    void getLeaderBoardCount(Map<String, String> filters, Double min, Double max) {
        when(redisTemplate.opsForZSet()).thenReturn(opsForZSet);
        accessAdService.getLeaderBoardCount(filters);
        verify(opsForZSet).count("leaderBoard", min, max);
    }

    public static Stream<Arguments> getLeaderBoardCount() {
        return Stream.of(
                Arguments.of(Map.of(), 0.0, -1.0),
                Arguments.of(Map.of("min", "3", "max", "10"),
                        3.0, 10.0));
    }
}
