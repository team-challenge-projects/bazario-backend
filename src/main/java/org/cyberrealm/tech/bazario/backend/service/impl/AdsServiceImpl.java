package org.cyberrealm.tech.bazario.backend.service.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.AdResponseDto;
import org.cyberrealm.tech.bazario.backend.dto.AdStatus;
import org.cyberrealm.tech.bazario.backend.exception.custom.ArgumentNotValidException;
import org.cyberrealm.tech.bazario.backend.mapper.AdMapper;
import org.cyberrealm.tech.bazario.backend.model.Ad;
import org.cyberrealm.tech.bazario.backend.repository.AdRepository;
import org.cyberrealm.tech.bazario.backend.service.AdParameterService;
import org.cyberrealm.tech.bazario.backend.service.AdsService;
import org.cyberrealm.tech.bazario.backend.service.UserParameterService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdsServiceImpl implements AdsService {
    private static final int INDEX_DIRECTION = 1;
    private static final int INDEX_FIELD_NAME = 0;
    public static final String DEFAULT_SORT = "id,asc";
    public static final String DELIMITER = ",";
    public static final String DEFAULT_PAGE = "0";
    public static final String DEFAULT_SIZE = "16";
    public static final String PATTER_SORT = "^[A-Za-z]+,(asc|desc)$";
    public static final int DEFAULT_INT_PAGE = 0;
    public static final int DEFAULT_INT_SIZE = 16;
    public static final String PREFIX_AD_PARAM = "ad_id_";
    public static final String REPLACEMENT = "";
    public static final String PREFIX_USER_PARAM = "user_id_";
    public static final int INDEX_KEY = 0;
    public static final int INDEX_VALUE = 1;

    private final UserParameterService userParameterService;
    private final AdParameterService adParameterService;
    private final AdRepository adRepository;
    private final AdMapper adMapper;


    @Override
    public Page<AdResponseDto> findAll(Map<String, String> filters) {
        Pageable pageable = getPageable(filters);
        Specification<Ad> spec = (root, query, builder) -> {
            var predicateByUser = getPredicateByUser(root, filters);
            var predicateByAdParam = getByAdParam(root, filters);
            var predicateByFields = getPredicateByFields(root, builder, filters);
            return builder.and(predicateByUser, predicateByAdParam, predicateByFields);
        };
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
                    if (value.contains("||")) {
                        var parts = value.split("\\|\\|");
                        var pricePredicate = builder.between(root.get("price"),
                                BigDecimal.valueOf(Double.parseDouble(parts[INDEX_KEY])),
                                BigDecimal.valueOf(Double.parseDouble(parts[INDEX_VALUE])));
                        predicate.add(pricePredicate);
                    }
                }
                case "publicationDate" -> {
                    if (value.contains("||")) {
                        var parts = value.split("\\|\\|");
                        var datePredicate = builder.between(root.get("publicationDate"),
                                LocalDate.parse(parts[INDEX_KEY]),
                                LocalDate.parse(parts[INDEX_VALUE]));
                        predicate.add(datePredicate);
                    }
                }
                case "category" -> {
                    predicate.add(builder.equal(root.get("category").get("id"),
                            Long.getLong(value)));
                }
                case "status" -> {
                    predicate.add(builder.equal(root.get("status"),
                            AdStatus.fromValue(value.toUpperCase())));
                }
            }
        });

        return  builder.and(predicate.toArray(jakarta.persistence.criteria.Predicate[]::new));
    }

    private jakarta.persistence.criteria.Predicate getByAdParam(
            Root<Ad> root, Map<String, String> filters) {
        var adParamFieldFilter = filters.entrySet().stream().filter(getParamPredicate(PREFIX_AD_PARAM))
                .collect(Collectors.toMap(entry ->
                                Long.getLong(entry.getKey().replaceFirst(PREFIX_AD_PARAM, REPLACEMENT)),
                        Map.Entry::getValue));
        var adIdsFiltered = adParameterService.filterByParam(adParamFieldFilter);
        return root.get("id").in(adIdsFiltered);
    }

    private jakarta.persistence.criteria.Predicate getPredicateByUser(
            Root<Ad> root, Map<String, String> filters) {
        var userParamFieldFilter = filters.entrySet().stream().filter(getParamPredicate(PREFIX_USER_PARAM))
                .collect(Collectors.toMap(entry ->
                                Long.getLong(entry.getKey().replaceFirst(PREFIX_USER_PARAM, REPLACEMENT)),
                        Map.Entry::getValue));
        var userIdsFiltered = userParameterService.filterByParam(userParamFieldFilter);
        return root.get("user").get("id").in(userIdsFiltered);
    }

    private Predicate<Map.Entry<String, String>> getParamPredicate(String prefix) {
        return entry -> {
            if (!entry.getKey().startsWith(prefix)) {
                return false;
            }
            Long adParamId;
            try {
                adParamId = Long.getLong(entry.getKey().replaceFirst(prefix, REPLACEMENT));
            } catch (Exception e) {
                adParamId = -1L;
            }
            return adParamId >= 0L;
        };
    }

    private Pageable getPageable(Map<String, String> filters) {
        String sort = Optional.ofNullable(filters.get("sort")).orElse(DEFAULT_SORT);
        if (!sort.matches(PATTER_SORT)) {
            throw new ArgumentNotValidException("String of sort contains: (fieldName,asc|desc)");
        }
        var parts = sort.split(DELIMITER);
        var direction = Sort.Direction.fromString(parts[INDEX_DIRECTION]);
        Integer page;
        try {
            page = Integer.getInteger(Optional.ofNullable(filters.get("page")).orElse(DEFAULT_PAGE));
        } catch (Exception e) {
            page = DEFAULT_INT_PAGE;
        }
        Integer size;
        try {
            size = Integer.getInteger(Optional.ofNullable(filters.get("size")).orElse(DEFAULT_SIZE));
        } catch (Exception e) {
            size = DEFAULT_INT_SIZE;
        }
        return PageRequest.of(page, size, Sort.by(direction, parts[INDEX_FIELD_NAME]));
    }
}
