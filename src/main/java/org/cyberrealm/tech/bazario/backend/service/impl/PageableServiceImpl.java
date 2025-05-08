package org.cyberrealm.tech.bazario.backend.service.impl;

import java.util.Map;
import java.util.Optional;
import org.cyberrealm.tech.bazario.backend.exception.custom.ArgumentNotValidException;
import org.cyberrealm.tech.bazario.backend.service.PageableService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class PageableServiceImpl implements PageableService {
    private static final int INDEX_DIRECTION = 1;
    private static final int INDEX_FIELD_NAME = 0;
    private static final String DEFAULT_SORT = "id,asc";
    private static final String DELIMITER = ",";
    private static final String DEFAULT_PAGE = "0";
    private static final String DEFAULT_SIZE = "16";
    private static final String PATTER_SORT = "^[A-Za-z]+,(asc|desc)$";
    private static final int DEFAULT_INT_PAGE = 0;
    private static final int DEFAULT_INT_SIZE = 16;

    @Override
    public Pageable get(Map<String, String> filters) {
        String sort = Optional.ofNullable(filters.get("sort")).orElse(DEFAULT_SORT);
        if (!sort.matches(PATTER_SORT)) {
            throw new ArgumentNotValidException("String of sort contains: (fieldName,asc|desc)");
        }
        var parts = sort.split(DELIMITER);
        var direction = Sort.Direction.fromString(parts[INDEX_DIRECTION]);
        int page;
        try {
            String pageString = Optional.ofNullable(filters.get("page"))
                    .orElse(DEFAULT_PAGE);
            page = Integer.parseInt(pageString);
        } catch (Exception e) {
            page = DEFAULT_INT_PAGE;
        }
        int size;
        try {
            String sizeString = Optional.ofNullable(filters.get("size"))
                    .orElse(DEFAULT_SIZE);
            size = Integer.parseInt(sizeString);
        } catch (Exception e) {
            size = DEFAULT_INT_SIZE;
        }
        return PageRequest.of(page, size, Sort.by(direction, parts[INDEX_FIELD_NAME]));
    }
}
