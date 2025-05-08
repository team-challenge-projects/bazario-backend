package org.cyberrealm.tech.bazario.backend.service.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Root;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.AdResponseDto;
import org.cyberrealm.tech.bazario.backend.dto.AdStatus;
import org.cyberrealm.tech.bazario.backend.mapper.AdMapper;
import org.cyberrealm.tech.bazario.backend.model.Ad;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.model.enums.Role;
import org.cyberrealm.tech.bazario.backend.repository.AdRepository;
import org.cyberrealm.tech.bazario.backend.service.AccessAdService;
import org.cyberrealm.tech.bazario.backend.service.AdParameterService;
import org.cyberrealm.tech.bazario.backend.service.AdsService;
import org.cyberrealm.tech.bazario.backend.service.PageableService;
import org.cyberrealm.tech.bazario.backend.service.UserParameterService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdsServiceImpl implements AdsService {
    private static final String PREFIX_AD_PARAM = "ad_id_";
    private static final String REPLACEMENT = "";
    private static final String PREFIX_USER_PARAM = "user_id_";
    private static final int INDEX_KEY = 0;
    private static final int INDEX_VALUE = 1;
    private static final String REGEX_BETWEEN_DELIMITER = "\\|\\|";
    private static final String BETWEEN_DELIMITER = "||";

    private final UserParameterService userParameterService;
    private final AdParameterService adParameterService;
    private final AdRepository adRepository;
    private final AdMapper adMapper;
    private final AccessAdService accessAdService;
    private final PageableService pageableService;

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

    private jakarta.persistence.criteria.Predicate getPredicateByFields(
            Root<Ad> root, CriteriaBuilder builder, Map<String, String> filters) {
        var predicate = new ArrayList<jakarta.persistence.criteria.Predicate>();
        if (!filters.containsKey("status")) {
            predicate.add(builder.equal(root.get("status"), AdStatus.ACTIVE));
        }
        filters.forEach((key, value) -> {
            switch (key) {
                case "price" -> {
                    if (value.contains(BETWEEN_DELIMITER)) {
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
                    if (value.contains(BETWEEN_DELIMITER)) {
                        try {
                            var parts = value.split(REGEX_BETWEEN_DELIMITER);
                            var datePredicate = builder.between(root.get("publicationDate"),
                                    LocalDate.parse(parts[INDEX_KEY]),
                                    LocalDate.parse(parts[INDEX_VALUE]));
                            predicate.add(datePredicate);
                        } catch (Exception e) {
                            builder.conjunction();
                        }
                    }
                }
                case "category" -> {
                    try {
                        predicate.add(builder.equal(root.get("category").get("id"),
                                Long.parseLong(value)));
                    } catch (NumberFormatException e) {
                        builder.conjunction();
                    }
                }
                case "user" -> {
                    try {
                        predicate.add(builder.equal(root.get("user").get("id"),
                                Long.parseLong(value)));
                    } catch (NumberFormatException e) {
                        builder.conjunction();
                    }
                }
                case "status" -> setPredicateStatus(root, builder, value, predicate);
                default -> builder.conjunction();
            }
        });
        return builder.and(predicate.toArray(jakarta.persistence.criteria.Predicate[]::new));
    }

    private void setPredicateStatus(Root<Ad> root, CriteriaBuilder builder, String value,
                                    ArrayList<jakarta.persistence.criteria.Predicate> predicate) {
        if (value.equals("*")) {
            if (accessAdService.isAdmin()) {
                return;
            } else if (accessAdService.isAuthenticationUser()) {
                predicate.add(builder.equal(root.get("status"), AdStatus.NEW));
                predicate.add(builder.equal(root.get("status"), AdStatus.ACTIVE));
                predicate.add(builder.equal(root.get("status"), AdStatus.DISABLE));
            } else {
                predicate.add(builder.equal(root.get("status"), AdStatus.ACTIVE));
            }
            return;
        }
        AdStatus status = AdStatus.fromValue(value.toUpperCase());
        if (accessAdService.isAdmin()
                || (!status.equals(AdStatus.DELETE)
                && accessAdService.isAuthenticationUser())) {
            predicate.add(builder.equal(root.get("status"),
                    status));
        } else {
            predicate.add(builder.equal(root.get("status"), AdStatus.ACTIVE));
        }

        User user = accessAdService.getUser();
        if (user.getRole().equals(Role.USER)
                && (status.equals(AdStatus.NEW) || status.equals(AdStatus.DISABLE))) {
            predicate.add(builder.equal(root.get("user"), user));
        }
    }

    private jakarta.persistence.criteria.Predicate getByAdParam(
            Root<Ad> root, CriteriaBuilder builder, Map<String, String> filters) {
        var adParamFieldFilter = filters.entrySet().stream()
                .filter(exceptNonNumeric(PREFIX_AD_PARAM))
                .collect(Collectors.toMap(entry ->
                                Long.parseLong(entry.getKey()
                                        .replaceFirst(PREFIX_AD_PARAM, REPLACEMENT)),
                        Map.Entry::getValue));
        if (adParamFieldFilter.isEmpty()) {
            return builder.conjunction();
        }
        var adIdsFiltered = adParameterService.filterByParam(adParamFieldFilter);
        return root.get("id").in(adIdsFiltered);
    }

    private jakarta.persistence.criteria.Predicate getPredicateByUser(
            Root<Ad> root, CriteriaBuilder builder, Map<String, String> filters) {
        var userParamFieldFilter = filters.entrySet().stream()
                .filter(exceptNonNumeric(PREFIX_USER_PARAM))
                .collect(Collectors.toMap(entry ->
                                Long.parseLong(entry.getKey()
                                        .replaceFirst(PREFIX_USER_PARAM, REPLACEMENT)),
                        Map.Entry::getValue));
        if (userParamFieldFilter.isEmpty()) {
            return builder.conjunction();
        }
        var userIdsFiltered = userParameterService.filterByParam(userParamFieldFilter);
        return root.get("user").get("id").in(userIdsFiltered);
    }

    private Predicate<Map.Entry<String, String>> exceptNonNumeric(String prefix) {
        return entry -> {
            if (!entry.getKey().startsWith(prefix)) {
                return false;
            }
            long adParamId;
            try {
                adParamId = Long.parseLong(entry.getKey().replaceFirst(prefix, REPLACEMENT));
            } catch (Exception e) {
                adParamId = -1L;
            }
            return adParamId >= 0L;
        };
    }
}
