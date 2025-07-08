package org.cyberrealm.tech.bazario.backend.service.impl;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.AdStatus;
import org.cyberrealm.tech.bazario.backend.exception.custom.EntityNotFoundException;
import org.cyberrealm.tech.bazario.backend.exception.custom.ForbiddenException;
import org.cyberrealm.tech.bazario.backend.model.Ad;
import org.cyberrealm.tech.bazario.backend.model.enums.Role;
import org.cyberrealm.tech.bazario.backend.repository.AdRepository;
import org.cyberrealm.tech.bazario.backend.service.AccessAdService;
import org.cyberrealm.tech.bazario.backend.service.AuthenticationUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccessAdServiceImpl implements AccessAdService {
    public static final String LEADER_BOARD = "leaderBoard";
    private final AdRepository adRepository;
    private final AuthenticationUserService authUserService;
    private final Map<String, Ad> cache = new ConcurrentHashMap<>();
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${leader-board.max-size}")
    private int maxSizeLeaderBoard;
    @Value("${leader-board.trial-period}")
    private long trialPeriod;
    @Value("${leader-board.count-delete-ad}")
    private int countDeleteAd;

    @Override
    public Ad getProtectedAd(Long id) {
        var ad = getAdFromCache(id);
        if (isNotAccessAd(ad)) {
            throw new ForbiddenException("User not access to ad by id " + id);
        }
        return ad;
    }

    private Ad getAdFromCache(Long id) {
        String key = "AD_ID_" + id;
        cache.computeIfAbsent(key, k -> adRepository.findByIdWithParameters(id)
                .orElseThrow(() -> new EntityNotFoundException("Ad by id " + id
                        + " not found")));
        var deleteList = incrementScoreOfLeaderBoardAndGetDeleteList(id);
        if (deleteList != null) {
            deleteList.forEach(cache::remove);
        }
        return cache.get(key);
    }

    private List<String> incrementScoreOfLeaderBoardAndGetDeleteList(Long key) {
        var set = redisTemplate.opsForZSet();
        var listForNewAd = redisTemplate.opsForValue();
        set.addIfAbsent(LEADER_BOARD, key, 0.0);
        set.incrementScore(LEADER_BOARD, key, 1.0);
        String board = "newAdsForLeaderBoard_";
        listForNewAd.setIfAbsent(board + key, key, Duration.ofDays(trialPeriod));
        if (set.size(LEADER_BOARD) > maxSizeLeaderBoard) {
            long end = countDeleteAd;
            long count = 0;
            Set<Object> range;
            do {
                end += count;
                range = set.range(LEADER_BOARD, 0, end);
                count = range != null ? range.stream().filter(e ->
                        listForNewAd.get(board + e) != null).count() : 0;
            } while (end < maxSizeLeaderBoard && (end - count) < countDeleteAd);
            var deleteList = range.stream().filter(e -> listForNewAd.get(board + e) == null)
                    .map(String.class::cast).toList();
            set.remove(LEADER_BOARD, deleteList);
            return deleteList;
        }
        return null;
    }

    @Override
    public void save(Ad ad) {
        adRepository.save(ad);
    }

    @Override
    public boolean isNotAccessAd(Ad ad) {
        if (!authUserService.isAuthenticationUser()) {
            return true;
        }
        var user = authUserService.getCurrentUser();
        return !(user.isAccountNonLocked() && (
                user.getRole().equals(Role.ROOT)
                        || user.getRole().equals(Role.ADMIN)
                        || (user.getRole().equals(Role.USER)
                        && ad.getUser().getId().equals(user.getId())
                        && !ad.getStatus().equals(AdStatus.DELETE))));
    }

    @Override
    public Ad getPublicAd(Long id) {
        var ad = getAdFromCache(id);
        if (!ad.getStatus().equals(AdStatus.ACTIVE) && isNotAccessAd(ad)) {
            throw new ForbiddenException("User not access to ad by id " + id);
        }
        return ad;
    }

    @Override
    public Set<ZSetOperations.TypedTuple<Object>> getLeaderBoardContent(Map<String,
            String> filter) {
        var set = redisTemplate.opsForZSet();

        var size = Long.parseLong(Optional.ofNullable(filter.get("size")).orElse("16"));
        var page = Long.parseLong(Optional.ofNullable(filter.get("page")).orElse("0"));

        var min = Double.parseDouble(Optional.ofNullable(filter.get("min")).orElse("0"));
        var max = Double.parseDouble(Optional.ofNullable(filter.get("max")).orElse("-1"));

        var sort = Optional.ofNullable(filter.get("sort")).orElse("desc");
        if (sort.equals("desc")) {
            return set.reverseRangeByScoreWithScores(LEADER_BOARD, min, max,
                            size * page, size);
        } else {
            return set.rangeByScoreWithScores(LEADER_BOARD, min, max,
                            size * page, size);
        }
    }

    @Override
    public long getLeaderBoardCount(Map<String, String> filter) {
        var set = redisTemplate.opsForZSet();

        var min = Double.parseDouble(Optional.ofNullable(filter.get("min")).orElse("0"));
        var max = Double.parseDouble(Optional.ofNullable(filter.get("max")).orElse("-1"));

        return Objects.requireNonNull(set.count(LEADER_BOARD, min, max));
    }
}
