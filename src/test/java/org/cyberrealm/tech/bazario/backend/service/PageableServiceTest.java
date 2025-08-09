package org.cyberrealm.tech.bazario.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import java.util.stream.Stream;
import org.cyberrealm.tech.bazario.backend.service.impl.PageableServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

class PageableServiceTest {
    private PageableServiceImpl pageableService;

    @BeforeEach
    void setUp() {
        pageableService = new PageableServiceImpl();
    }

    @ParameterizedTest
    @MethodSource
    void get(Map<String, String> filter, Pageable pageable) {
        var newPageable = pageableService.get(filter);

        var order = pageable.getSort().get().findFirst().orElseThrow();
        var newOrder = newPageable.getSort().get().findFirst().orElseThrow();

        assertEquals(pageable.getPageSize(), newPageable.getPageSize());
        assertEquals(pageable.getPageNumber(), newPageable.getPageNumber());
        assertEquals(order.getDirection().name(), newOrder.getDirection().name());
        assertEquals(order.getProperty(), newOrder.getProperty());
    }

    public static Stream<Arguments> get() {
        return Stream.of(
                Arguments.of(Map.of(), PageRequest.of(0, 16, Sort.by(Sort.Direction.ASC, "id"))),
                Arguments.of(Map.of("page", "1", "size", "20", "sort", "price,desc"),
                        PageRequest.of(1, 20, Sort.by(Sort.Direction.DESC, "price")))
        );
    }
}
