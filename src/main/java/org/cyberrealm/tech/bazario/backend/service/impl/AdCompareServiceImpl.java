package org.cyberrealm.tech.bazario.backend.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.AdComparesDto;
import org.cyberrealm.tech.bazario.backend.dto.ItemComparesDto;
import org.cyberrealm.tech.bazario.backend.dto.PageCompareAd;
import org.cyberrealm.tech.bazario.backend.dto.PageCompareAdMinMaxInner;
import org.cyberrealm.tech.bazario.backend.dto.user.UserScore;
import org.cyberrealm.tech.bazario.backend.model.Ad;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.repository.AdRepository;
import org.cyberrealm.tech.bazario.backend.repository.CommentRepository;
import org.cyberrealm.tech.bazario.backend.repository.UserRepository;
import org.cyberrealm.tech.bazario.backend.service.AdCompareService;
import org.cyberrealm.tech.bazario.backend.service.AuthenticationUserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdCompareServiceImpl implements AdCompareService {
    private static final String PRICE_MIN = "priceMin";
    private static final String PRICE_MAX = "priceMax";
    private static final String RATING_MIN = "ratingMin";
    private static final String RATING_MAX = "ratingMax";
    private static final String DISTANCE_MIN = "distanceMin";
    private static final String DISTANCE_MAX = "distanceMax";
    private static final String PRICE = "price";
    private static final String RATING = "rating";
    private static final String DISTANCE = "distance";

    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final AuthenticationUserService authService;
    private final CommentRepository commentRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public PageCompareAd compares(List<Long> ids) {
        var ads = adRepository.findAllById(ids);
        var userIds = ads.stream().map(ad -> ad.getUser().getId())
                .distinct().toList();
        var distances = getDistances(userIds);

        var ratings = commentRepository.getAvgRatingByUserIds(userIds);
        var ratingMap = ratings.stream().collect(Collectors.toMap(UserScore::id, UserScore::score));

        var minMaxMap = getMinMaxMap(ads, ratings, distances);
        var minMaxList = List.of(
                new PageCompareAdMinMaxInner().name(PRICE).min(minMaxMap.get(PRICE_MIN))
                        .max(minMaxMap.get(PRICE_MAX)),
                new PageCompareAdMinMaxInner().name(RATING).min(minMaxMap.get(RATING_MIN))
                        .max(minMaxMap.get(RATING_MAX)),
                new PageCompareAdMinMaxInner().name(DISTANCE).min(minMaxMap.get(DISTANCE_MIN))
                        .max(minMaxMap.get(DISTANCE_MAX))
                );

        var content = ads.stream().map(ad -> {
            ItemComparesDto price = new ItemComparesDto().name(PRICE)
                    .score(ad.getPrice().doubleValue())
                    .percent((ad.getPrice().doubleValue() - minMaxMap.get(PRICE_MIN))
                            / (minMaxMap.get(PRICE_MAX) - minMaxMap.get(PRICE_MIN)));
            ItemComparesDto rating = new ItemComparesDto().name(RATING)
                    .score(ratingMap.get(ad.getUser().getId()))
                    .percent((ratingMap.get(ad.getUser().getId()) - minMaxMap.get(RATING_MIN))
                            / (minMaxMap.get(RATING_MAX) - minMaxMap.get(RATING_MIN)));
            ItemComparesDto distance = new ItemComparesDto().name(DISTANCE)
                    .score(distances.get(ad.getUser().getId()))
                    .percent((distances.get(ad.getUser().getId()) - minMaxMap.get(DISTANCE_MIN))
                            / (minMaxMap.get(DISTANCE_MAX) - minMaxMap.get(DISTANCE_MIN)));
            return new AdComparesDto().id(ad.getId()).title(ad.getTitle())
                    .description(ad.getDescription()).price(ad.getPrice())
                    .category(ad.getCategory().getId())
                    .compares(List.of(price, rating, distance));
        }).toList();
        return new PageCompareAd().content(content).minMax(minMaxList);
    }

    private Map<Long, Double> getDistances(List<Long> userIds) {
        var currentCityName = userRepository.findById(authService.getCurrentUser().getId())
                .orElseThrow().getCityName();
        var users = userRepository.findAllById(userIds);
        return users.stream().collect(Collectors.toMap(User::getId,
                user -> Objects.requireNonNull(redisTemplate.opsForGeo()
                                .distance("city", currentCityName, user.getCityName()))
                        .getValue()));
    }

    private Map<String, Double> getMinMaxMap(
            List<Ad> ads, List<UserScore> ratings, Map<Long, Double> distances) {
        Comparator<Ad> comparatorPrice = Comparator.comparingDouble(ad ->
                ad.getPrice().doubleValue());
        var minPrice = ads.stream().min(comparatorPrice).orElseThrow()
                .getPrice().doubleValue();
        var maxPrice = ads.stream().max(comparatorPrice).orElseThrow()
                .getPrice().doubleValue();

        Comparator<UserScore> comparatorRating = Comparator.comparingDouble(UserScore::score);
        var minRating = ratings.stream().min(comparatorRating).orElseThrow().score();
        var maxRating = ratings.stream().max(comparatorRating).orElseThrow().score();

        var valueDistance = distances.values();
        var minDistance = valueDistance.stream().min(Double::compareTo).orElseThrow();
        var maxDistance = valueDistance.stream().max(Double::compareTo).orElseThrow();

        return Map.of(PRICE_MIN, minPrice, PRICE_MAX, maxPrice,
                RATING_MIN, minRating, RATING_MAX, maxRating,
                DISTANCE_MIN, minDistance, DISTANCE_MAX, maxDistance);
    }
}
