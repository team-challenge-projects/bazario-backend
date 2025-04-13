package org.cyberrealm.tech.bazario.backend.mapper;

import java.net.URI;
import java.util.List;
import java.util.Set;
import org.cyberrealm.tech.bazario.backend.config.MapperConfig;
import org.cyberrealm.tech.bazario.backend.dto.AdDto;
import org.cyberrealm.tech.bazario.backend.dto.AdResponseDto;
import org.cyberrealm.tech.bazario.backend.dto.PatchAd;
import org.cyberrealm.tech.bazario.backend.dto.ad.CreateAdRequestDto;
import org.cyberrealm.tech.bazario.backend.model.Ad;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.openapitools.jackson.nullable.JsonNullable;

@Mapper(config = MapperConfig.class)
public interface AdMapper {

    AdDto toDto(Ad ad);

    @Mapping(target = "imageUrl", expression =
            "java(URI.create(ad.getImages().stream().findFirst().orElse(\"test/test.jpg\")))")
    AdResponseDto toResponseDto(Ad ad);

    Ad toEntity(CreateAdRequestDto requestDto);

    List<URI> toListUri(Set<String> uris);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publicationDate", ignore = true)
    @Mapping(target = "images", ignore = true)
    void updateAdFromDto(PatchAd patchAd, @MappingTarget Ad ad);

    default URI mapStringToUri(String value) {
        return URI.create(value);
    }

    default <T> T mapJsonNullableToString(JsonNullable<T> value) {
        return value.orElse(null);
    }
}
