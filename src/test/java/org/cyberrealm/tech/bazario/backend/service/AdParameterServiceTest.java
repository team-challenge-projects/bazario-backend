package org.cyberrealm.tech.bazario.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.cyberrealm.tech.bazario.backend.AbstractIntegrationTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

class AdParameterServiceTest extends AbstractIntegrationTest {
    @Autowired
    private AdParameterService parameterService;

    @ParameterizedTest
    @MethodSource
    void filterByParam(Map<Long, String> filters, List<Long> adIds) throws JsonProcessingException {
        var list = parameterService.filterByParam(filters);
        assertEquals(objectMapper.writeValueAsString(adIds),
                objectMapper.writeValueAsString(list));
    }

    public static Stream<Arguments> filterByParam() {
        return Stream.of(
                Arguments.of(
                        Map.of(1L, "ТестПошта|ТестовийСклад"),
                        List.of(1L, 2L)
                ),
                Arguments.of(
                        Map.of(1L, "ТестПошта"),
                        List.of(1L)
                ),
                Arguments.of(
                        Map.of(1L, "ТестовийСклад"),
                        List.of(2L)
                )
        );
    }
}
