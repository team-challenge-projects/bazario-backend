package org.cyberrealm.tech.bazario.backend.service.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Root;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.AdResponseDto;
import org.cyberrealm.tech.bazario.backend.dto.AdStatus;
import org.cyberrealm.tech.bazario.backend.mapper.AdMapper;
import org.cyberrealm.tech.bazario.backend.model.Ad;
import org.cyberrealm.tech.bazario.backend.model.enums.Role;
import org.cyberrealm.tech.bazario.backend.repository.AdRepository;
import org.cyberrealm.tech.bazario.backend.service.AdParameterService;
import org.cyberrealm.tech.bazario.backend.service.AdsService;
import org.cyberrealm.tech.bazario.backend.service.AuthenticationUserService;
import org.cyberrealm.tech.bazario.backend.service.CommentService;
import org.cyberrealm.tech.bazario.backend.service.PageableService;
import org.cyberrealm.tech.bazario.backend.service.UserParameterService;
import org.cyberrealm.tech.bazario.backend.service.UserService;
import org.cyberrealm.tech.bazario.backend.util.GeometryUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdsServiceImpl implements AdsService {
    private static final String PREFIX_AD_PARAM = "ad_id_";
    private static final String EMPTY = "";
    private static final String PREFIX_USER_PARAM = "user_id_";
    private static final int INDEX_FROM = 0;
    private static final int INDEX_TO = 1;
    private static final int INDEX_KEY = 0;
    private static final int INDEX_VALUE = 1;
    private static final String REGEX_BETWEEN_DELIMITER = "\\|\\|";
    private static final String REGEX_BETWEEN = "^\\d*\\.?\\d+\\|\\|\\d*\\.?\\d+$";
    private static final String REGEX_DOUBLE = "^\\d*\\.?\\d+$";
    private static final String REGEX_NUMBER = "^\\d+$";
    private static final int FACTOR_METER_TO_KILOMETER = 1000;
    private static final String REGEX_BETWEEN_DATE =
            "^\\d{4}-\\d{2}-\\d{2}\\|\\|\\d{4}-\\d{2}-\\d{2}$";

    private final UserParameterService userParameterService;
    private final AdParameterService adParameterService;
    private final AdRepository adRepository;
    private final AdMapper adMapper;
    private final AuthenticationUserService authUserService;
    private final PageableService pageableService;
    private final CommentService commentService;
    private final UserService userService;

    @Override
    public Page<AdResponseDto> findAll(Map<String, String> filters) {
        Pageable pageable = pageableService.get(filters);
        Specification<Ad> spec = (root, query, builder) ->
                builder.and(
                        getPredicateByUser(root, builder, filters),
                        getByAdParam(root, builder, filters),
                        getPredicateByFields(root, builder, filters));

        return adRepository.findAll(spec, pageable).map(adMapper::toResponseDto);
    }

    @Override
    public Page<AdResponseDto> findAllForUser(Map<String, String> filters) {
        Pageable pageable = pageableService.get(filters);
        Specification<Ad> spec = (root, query, builder) ->
                builder.and(
                        getPredicateByUser(root, builder, filters),
                        getByAdParam(root, builder, filters),
                        getPredicateByFields(root, builder, filters),
                        getPredicateByFieldsForUser(root, builder, filters));

        return adRepository.findAll(spec, pageable).map(ad ->
                adMapper.toResponseDto(ad).distance(
                        GeometryUtil.haversine(authUserService.getCurrentUser().getCityCoordinate(),
                                ad.getCityCoordinate())));
    }

    private jakarta.persistence.criteria.Predicate getPredicateByFieldsForUser(
            Root<Ad> root, CriteriaBuilder builder, Map<String, String> filters) {
        var predicate = new ArrayList<jakarta.persistence.criteria.Predicate>();
        filters.forEach((key, value) -> {
            switch (key) {
                case "distance" -> {
                    if (value.matches(REGEX_DOUBLE)) {
                        var startPoint = authUserService.getCurrentUser().getCityCoordinate();
                        predicate.add(builder.lt(builder.function("ST_DistanceSphere",
                                        Double.class, root.get("cityCoordinate"),
                                        builder.literal(startPoint)),
                                builder.literal(Double.parseDouble(value)
                                        * FACTOR_METER_TO_KILOMETER)));
                    }
                }
                case "status" -> setPredicateStatus(root, builder, value, predicate);
                default -> {
                }
            }
        });
        return builder.and(predicate.toArray(jakarta.persistence.criteria.Predicate[]::new));
    }

    private jakarta.persistence.criteria.Predicate getPredicateByFields(
            Root<Ad> root, CriteriaBuilder builder, Map<String, String> filters) {
        var predicate = new ArrayList<jakarta.persistence.criteria.Predicate>();
        if (!filters.containsKey("status")) {
            predicate.add(builder.equal(root.get("status"), AdStatus.ACTIVE));
        }
        filters.forEach((key, value) -> {
            switch (key) {
                case "price" -> {
                    if (value.matches(REGEX_BETWEEN)) {
                        try {
                            var parts = value.split(REGEX_BETWEEN_DELIMITER);
                            var pricePredicate = builder.between(root.get("price"),
                                    BigDecimal.valueOf(Double.parseDouble(parts[INDEX_KEY])),
                                    BigDecimal.valueOf(Double.parseDouble(parts[INDEX_VALUE])));
                            predicate.add(pricePredicate);
                        } catch (NumberFormatException e) {
                            builder.conjunction();
                        }
                    }
                }
                case "publicationDate" -> {
                    if (value.matches(REGEX_BETWEEN_DATE)) {
                        var parts = value.split(REGEX_BETWEEN_DELIMITER);
                        var datePredicate = builder.between(root.get("publicationDate"),
                                LocalDate.parse(parts[INDEX_KEY]),
                                LocalDate.parse(parts[INDEX_VALUE]));
                        predicate.add(datePredicate);
                    }
                }
                case "title" -> predicate.add(builder.like(root.get("title"), "%" + value + "%"));
                case "category" -> {
                    if (value.matches(REGEX_NUMBER)) {
                        predicate.add(builder.equal(root.get("category").get("id"),
                                Long.parseLong(value)));

                    }
                }
                default -> {
                }
            }
        });
        return builder.and(predicate.toArray(jakarta.persistence.criteria.Predicate[]::new));
    }

    private void setPredicateStatus(Root<Ad> root, CriteriaBuilder builder, String value,
                                    ArrayList<jakarta.persistence.criteria.Predicate> predicate) {
        boolean isFilterUser = false;
        if (value.equals("*")) {
            if (!authUserService.isAdmin()) {
                if (authUserService.isAuthenticationUser()) {
                    predicate.add(root.get("status").in(AdStatus.NEW, AdStatus.ACTIVE,
                            AdStatus.DISABLE));
                    isFilterUser = true;
                } else {
                    predicate.add(builder.equal(root.get("status"), AdStatus.ACTIVE));
                }
            }
        } else {
            AdStatus status = Arrays.stream(AdStatus.values())
                    .filter(s -> s.getValue().equals(value.toUpperCase()))
                    .findFirst().orElse(AdStatus.ACTIVE);

            if (authUserService.isAdmin()
                    || (!status.equals(AdStatus.DELETE)
                    && authUserService.isAuthenticationUser())) {
                predicate.add(builder.equal(root.get("status"),
                        status));
                isFilterUser = authUserService.getCurrentUser().getRole().equals(Role.USER)
                        && (status.equals(AdStatus.NEW) || status.equals(AdStatus.DISABLE));
            } else {
                predicate.add(builder.equal(root.get("status"), AdStatus.ACTIVE));
            }
        }

        if (isFilterUser) {
            predicate.add(builder.equal(root.get("user").get("id"),
                    authUserService.getCurrentUser().getId()));
        }
    }

    private jakarta.persistence.criteria.Predicate getByAdParam(
            Root<Ad> root, CriteriaBuilder builder, Map<String, String> filters) {
        var adParamFieldFilter = filters.entrySet().stream()
                .filter(exceptNonNumeric(PREFIX_AD_PARAM))
                .collect(Collectors.toMap(entry ->
                                Long.parseLong(entry.getKey()
                                        .replaceFirst(PREFIX_AD_PARAM, EMPTY)),
                        Map.Entry::getValue));
        if (adParamFieldFilter.isEmpty()) {
            return builder.conjunction();
        }
        var adIdsFiltered = adParameterService.filterByParam(adParamFieldFilter);
        return root.get("id").in(adIdsFiltered);
    }

    private jakarta.persistence.criteria.Predicate getPredicateByUser(
            Root<Ad> root, CriteriaBuilder builder, Map<String, String> filters) {
        Set<Long> userIds = new HashSet<>();
        if (filters.containsKey("user") && filters.get("user").matches(REGEX_NUMBER)) {
            return builder.equal(root.get("user").get("id"),
                    Long.parseLong(filters.get("user")));
        }
        var userParamFieldFilter = filters.entrySet().stream()
                .filter(exceptNonNumeric(PREFIX_USER_PARAM))
                .collect(Collectors.toMap(entry ->
                                Long.parseLong(entry.getKey()
                                        .replaceFirst(PREFIX_USER_PARAM, EMPTY)),
                        Map.Entry::getValue));

        if (!userParamFieldFilter.isEmpty()) {
            userIds.addAll(userParameterService.filterByParam(userParamFieldFilter));
        }

        filters.entrySet().stream().filter(entry -> entry.getKey()
                        .equals("rating") && entry.getValue().matches(REGEX_BETWEEN))
                .map(Map.Entry::getValue).findFirst()
                .ifPresent(rating -> {
                    var range = rating.split(REGEX_BETWEEN_DELIMITER);

                    List<Long> byRangeRating = commentService.getUserIdsByRangeRating(
                            Integer.parseInt(range[INDEX_FROM]),
                            Integer.parseInt(range[INDEX_TO]));
                    if (userIds.isEmpty()) {
                        userIds.addAll(byRangeRating);
                    } else {
                        userIds.stream().filter(id -> !byRangeRating.contains(id))
                                .forEach(userIds::remove);
                    }
                });
        if (authUserService.isAuthenticationUser()) {
            filters.entrySet().stream().filter(entry -> entry.getKey()
                            .equals("user_distance") && entry.getValue().matches(REGEX_DOUBLE))
                    .map(e -> Double.parseDouble(e.getValue())).findFirst()
                    .ifPresent(distance -> {
                        var ids = userService.getUserIdByDistance(distance);
                        if (userIds.isEmpty()) {
                            userIds.addAll(ids);
                        } else {
                            userIds.stream().filter(id -> !ids.contains(id))
                                    .forEach(userIds::remove);
                        }
                    });
        }

        return userIds.isEmpty() ? builder.conjunction() : root.get("user").get("id").in(userIds);
    }

    private Predicate<Map.Entry<String, String>> exceptNonNumeric(String prefix) {
        return entry -> {
            var regex = "^%s\\d+$".formatted(prefix);
            return entry.getKey().matches(regex);
        };
    }
}
