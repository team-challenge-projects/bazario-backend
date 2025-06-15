package org.cyberrealm.tech.bazario.backend.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.cyberrealm.tech.bazario.backend.AbstractIntegrationTest;
import org.cyberrealm.tech.bazario.backend.dto.AdResponseDto;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

class AdsServiceTest extends AbstractIntegrationTest {
    private static final Pageable pageable = PageRequest.of(0, 16,
            Sort.by("id").ascending());
    @Autowired
    private AdsService service;

    @ParameterizedTest
    @MethodSource
    void findAll(Map<String, String> filters, Page<AdResponseDto> page) {
        Page<AdResponseDto> dtoPage = service.findAll(filters);
        AdResponseDto responseOne = page.getContent().get(0);
        AdResponseDto dtoOne = dtoPage.getContent().get(0);

        assertAll(
                () -> assertEquals(dtoPage.getTotalElements(), page.getTotalElements()),
                () -> assertEquals(dtoPage.getContent().size(), page.getContent().size()),
                () -> assertEquals(dtoOne.getId(), responseOne.getId()),
                () -> assertEquals(dtoOne.getTitle(), responseOne.getTitle()),
                () -> assertEquals(Objects.requireNonNull(dtoOne.getPrice()).doubleValue(),
                        Objects.requireNonNull(responseOne.getPrice()).doubleValue())
        );
    }

    public static Stream<Arguments> findAll() {
        return Stream.of(
                Arguments.of(Map.of(),
                        new PageImpl<AdResponseDto>(
                                List.of(DtoAd.ONE.getDto(),
                                        DtoAd.TWO.getDto(),
                                        DtoAd.THREE.getDto()), pageable, ID_THREE)),
                Arguments.of(Map.of("title","search"),
                        new PageImpl<AdResponseDto>(
                                List.of(DtoAd.TWO.getDto()), pageable, ID_ONE)),
                Arguments.of(Map.of("price","1000.00||3000.00"),
                        new PageImpl<AdResponseDto>(
                                List.of(DtoAd.ONE.getDto()), pageable, ID_ONE)),
                Arguments.of(Map.of("publicationDate","2025-01-01||2025-02-06"),
                        new PageImpl<AdResponseDto>(
                                List.of(DtoAd.THREE.getDto()), pageable, ID_ONE)),
                Arguments.of(Map.of("ad_id_1","ТестПошта"),
                        new PageImpl<AdResponseDto>(
                                List.of(DtoAd.ONE.getDto()), pageable, ID_ONE)),
                Arguments.of(Map.of("ad_id_1","ТестПошта|ТестовийСклад"),
                        new PageImpl<AdResponseDto>(
                                List.of(DtoAd.ONE.getDto(), DtoAd.TWO.getDto()),
                                pageable, ID_TWO)),
                Arguments.of(Map.of("user_id_1","ТестТип"),
                        new PageImpl<AdResponseDto>(
                                List.of(DtoAd.ONE.getDto()), pageable, ID_ONE)),
                Arguments.of(Map.of("user_id_1","ТестТип|ЮридичнийТип"),
                        new PageImpl<AdResponseDto>(
                                List.of(DtoAd.ONE.getDto(), DtoAd.TWO.getDto()),
                                pageable, ID_TWO)),
                Arguments.of(Map.of("rating","4||7"),
                        new PageImpl<AdResponseDto>(
                                List.of(DtoAd.ONE.getDto()), pageable, ID_ONE)),
                Arguments.of(Map.of("user","1"),
                        new PageImpl<AdResponseDto>(
                                List.of(DtoAd.ONE.getDto()), pageable, ID_ONE)),
                Arguments.of(Map.of("category","2"),
                        new PageImpl<AdResponseDto>(
                                List.of(DtoAd.TWO.getDto()), pageable, ID_ONE)));
    }

    @AllArgsConstructor
    @Getter
    enum DtoAd {
        ONE(new AdResponseDto().title("Тест").description("Тест")
                .id(ID_ONE).price(BigDecimal.valueOf(1000.00))
                .category(ID_ONE).imageUrl(URI.create(""))),
        TWO(new AdResponseDto().title("Тест search text")
                .description("Тест search text")
                .id(ID_TWO).price(BigDecimal.valueOf(3500.00))
                .category(ID_TWO).imageUrl(URI.create(""))),
        THREE(new AdResponseDto().title("Тест").description("Тест")
                .id(ID_THREE).price(BigDecimal.valueOf(5000.00))
                .category(ID_ONE).imageUrl(URI.create("")));

        private final AdResponseDto dto;
    }
}
