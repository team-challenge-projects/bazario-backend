package org.cyberrealm.tech.bazario.backend.mapper;

import java.net.URI;
import java.util.List;
import java.util.Set;
import org.cyberrealm.tech.bazario.backend.config.MapperConfig;
import org.cyberrealm.tech.bazario.backend.dto.AdDto;
import org.cyberrealm.tech.bazario.backend.dto.AdResponseDto;
import org.cyberrealm.tech.bazario.backend.dto.BasicUserParameter;
import org.cyberrealm.tech.bazario.backend.dto.PatchAd;
import org.cyberrealm.tech.bazario.backend.model.Ad;
import org.cyberrealm.tech.bazario.backend.model.AdParameter;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = MapperConfig.class)
public interface AdMapper {

    @Mapping(target = "adParameters", source = "parameters")
    AdDto toDto(Ad ad);

    @Mapping(target = "imageUrl", expression =
            "java(URI.create(ad.getImages().stream().findFirst().orElse(\"test/test.jpg\")))")
    AdResponseDto toResponseDto(Ad ad);

    List<URI> toListUri(Set<String> uris);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publicationDate", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "parameters", source = "adParameters")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAdFromDto(PatchAd patchAd, @MappingTarget Ad ad);

    @Mapping(target = "ad", ignore = true)
    @Mapping(target = "parameter.id", source = "parameterId")
    AdParameter toAdParameterFromDto(BasicUserParameter dto);

    @Mapping(target = "parameterId", source = "parameter.id")
    @Mapping(target = "name", source = "parameter.name")
    BasicUserParameter toDtoFromAdParameter(AdParameter adParameter);

    default URI mapStringToUri(String value) {
        return URI.create(value);
    }
}
