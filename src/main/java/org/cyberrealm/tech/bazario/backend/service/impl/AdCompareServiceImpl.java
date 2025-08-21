package org.cyberrealm.tech.bazario.backend.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
import org.cyberrealm.tech.bazario.backend.util.GeometryUtil;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdCompareServiceImpl implements AdCompareService {
    private static final String PRICE_MIN = "priceMin";
    private static final String PRICE_MAX = "priceMax";
    private static final String RATING_MIN = "ratingMin";
    private static final String RATING_MAX = "ratingMax";
    private static final String USER_DISTANCE_MIN = "userDistanceMin";
    private static final String USER_DISTANCE_MAX = "userDistanceMax";
    private static final String PRICE = "price";
    private static final String RATING = "rating";
    private static final String USER_DISTANCE = "userDistance";
    private static final String DISTANCE_MIN = "distanceMin";
    private static final String DISTANCE_MAX = "distanceMax";
    private static final String DISTANCE = "distance";

    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final AuthenticationUserService authService;
    private final CommentRepository commentRepository;

    @Override
    public PageCompareAd compares(List<Long> ids) {
        var ads = adRepository.findAllById(ids);
        var userIds = ads.stream().map(ad -> ad.getUser().getId())
                .distinct().toList();
        var userDistances = getUserDistances(userIds);
        var distances = getDistances(ads);

        var ratings = commentRepository.getAvgRatingByUserIds(userIds);
        var ratingMap = ratings.stream().collect(Collectors.toMap(UserScore::id, UserScore::score));

        var minMaxMap = getMinMaxMap(ads, ratings, distances, userDistances);
        var minMaxList = List.of(
                new PageCompareAdMinMaxInner().name(PRICE).min(minMaxMap.get(PRICE_MIN))
                        .max(minMaxMap.get(PRICE_MAX)),
                new PageCompareAdMinMaxInner().name(RATING).min(minMaxMap.get(RATING_MIN))
                        .max(minMaxMap.get(RATING_MAX)),
                new PageCompareAdMinMaxInner().name(DISTANCE).min(minMaxMap.get(DISTANCE_MIN))
                        .max(minMaxMap.get(DISTANCE_MAX)),
                new PageCompareAdMinMaxInner().name(USER_DISTANCE)
                        .min(minMaxMap.get(USER_DISTANCE_MIN))
                        .max(minMaxMap.get(USER_DISTANCE_MAX))
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
                    .score(distances.get(ad.getId()))
                    .percent((distances.get(ad.getId()) - minMaxMap.get(DISTANCE_MIN))
                            / (minMaxMap.get(DISTANCE_MAX) - minMaxMap.get(DISTANCE_MIN)));
            ItemComparesDto userDistance = new ItemComparesDto().name(USER_DISTANCE)
                    .score(userDistances.get(ad.getUser().getId()))
                    .percent((userDistances.get(ad.getUser().getId())
                            - minMaxMap.get(USER_DISTANCE_MIN))
                            / (minMaxMap.get(USER_DISTANCE_MAX)
                            - minMaxMap.get(USER_DISTANCE_MIN)));
            return new AdComparesDto().id(ad.getId()).title(ad.getTitle())
                    .description(ad.getDescription()).price(ad.getPrice())
                    .category(ad.getCategory().getId())
                    .compares(List.of(price, rating, distance, userDistance));
        }).toList();
        return new PageCompareAd().content(content).minMax(minMaxList);
    }

    private Map<Long, Double> getDistances(List<Ad> ads) {
        var startPoint = authService.getCurrentUser().getCityCoordinate();
        return ads.stream().collect(Collectors.toMap(Ad::getId, ad ->
                GeometryUtil.haversine(startPoint, ad.getCityCoordinate())));
    }

    private Map<Long, Double> getUserDistances(List<Long> userIds) {
        var currentUser = authService.getCurrentUser();
        var users = userRepository.findAllById(userIds);
        return users.stream().collect(Collectors.toMap(User::getId, user ->
                GeometryUtil.haversine(currentUser.getCityCoordinate(),
                        user.getCityCoordinate())));
    }

    private Map<String, Double> getMinMaxMap(
            List<Ad> ads, List<UserScore> ratings, Map<Long, Double> distances,
            Map<Long, Double> userDistances) {
        Comparator<Ad> comparatorPrice = Comparator.comparingDouble(ad ->
                ad.getPrice().doubleValue());
        var minPrice = ads.stream().min(comparatorPrice).orElseThrow()
                .getPrice().doubleValue();
        var maxPrice = ads.stream().max(comparatorPrice).orElseThrow()
                .getPrice().doubleValue();

        Comparator<UserScore> comparatorRating = Comparator.comparingDouble(UserScore::score);
        var minRating = ratings.stream().min(comparatorRating).orElseThrow().score();
        var maxRating = ratings.stream().max(comparatorRating).orElseThrow().score();

        var valueUserDistance = userDistances.values();
        var minUserDistance = valueUserDistance.stream().min(Double::compareTo).orElseThrow();
        var maxUserDistance = valueUserDistance.stream().max(Double::compareTo).orElseThrow();

        var valuesDistance = distances.values();
        var minDistance = valuesDistance.stream().min(Double::compareTo).orElseThrow();
        var maxDistance = valuesDistance.stream().max(Double::compareTo).orElseThrow();

        return Map.of(PRICE_MIN, minPrice, PRICE_MAX, maxPrice,
                RATING_MIN, minRating, RATING_MAX, maxRating,
                DISTANCE_MIN, minDistance, DISTANCE_MAX, maxDistance,
                USER_DISTANCE_MIN, minUserDistance, USER_DISTANCE_MAX, maxUserDistance);
    }
}
