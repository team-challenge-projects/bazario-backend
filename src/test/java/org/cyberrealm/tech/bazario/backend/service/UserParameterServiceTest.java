package org.cyberrealm.tech.bazario.backend.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.cyberrealm.tech.bazario.backend.AbstractIntegrationTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

class UserParameterServiceTest extends AbstractIntegrationTest {
    @Autowired
    private UserParameterService parameterService;

    @ParameterizedTest
    @MethodSource
    void filterByParam(Map<Long, String> filters, List<Long> userIds) {
    }

    public static Stream<Arguments> filterByParam() {
        return Stream.of(
                Arguments.of(Map.of(1L, "ТестТип|ЮридичнийТип"),
                        List.of(1L, 2L)),
                Arguments.of(Map.of(1L, "ТестТип"), List.of(1L)),
                Arguments.of(Map.of(1L, "ЮридичнийТип"), List.of(2L))
        );
    }
}
