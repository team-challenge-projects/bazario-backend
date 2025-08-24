package org.cyberrealm.tech.bazario.backend.mapper;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.cyberrealm.tech.bazario.backend.config.MapperConfig;
import org.cyberrealm.tech.bazario.backend.dto.AdComparesDto;
import org.cyberrealm.tech.bazario.backend.dto.AdDto;
import org.cyberrealm.tech.bazario.backend.dto.AdResponseDto;
import org.cyberrealm.tech.bazario.backend.dto.BasicUserParameter;
import org.cyberrealm.tech.bazario.backend.dto.PatchAd;
import org.cyberrealm.tech.bazario.backend.dto.script.AdCredentials;
import org.cyberrealm.tech.bazario.backend.model.Ad;
import org.cyberrealm.tech.bazario.backend.model.AdParameter;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = MapperConfig.class)
public interface AdMapper {

    @Mapping(target = "adParameters", source = "ad.parameters")
    @Mapping(target = "cityCoordinate", expression =
            "java(ad.getCityCoordinate() != null ? ad.getCityCoordinate().toText() : \"null\")")
    @Mapping(target = "distance", source = "distance")
    AdDto toDto(Ad ad, double distance);

    @Mapping(target = "imageUrl", expression =
            "java(URI.create(ad.getImages().stream().findFirst().orElse(\"\")))")

    @Mapping(target = "category", source = "category.id")
    AdResponseDto toResponseDto(Ad ad);

    List<URI> toListUri(Set<String> uris);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publicationDate", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "parameters", ignore = true)
    @Mapping(target = "cityCoordinate", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAdFromDto(PatchAd patchAd, @MappingTarget Ad ad);

    @Mapping(target = "ad", ignore = true)
    @Mapping(target = "parameter.id", source = "typeId")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAdParameterFromDto(BasicUserParameter dto, @MappingTarget AdParameter adParameter);

    @Mapping(target = "ad", ignore = true)
    @Mapping(target = "parameter.id", source = "typeId")
    AdParameter toAdParameterFromDto(BasicUserParameter dto);

    @Mapping(target = "typeId", source = "parameter.id")
    @Mapping(target = "typeName", source = "parameter.name")
    BasicUserParameter toDtoFromAdParameter(AdParameter adParameter);

    default URI mapStringToUri(String value) {
        return URI.create(value);
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publicationDate", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "parameters", ignore = true)
    @Mapping(target = "cityCoordinate", ignore = true)
    Ad toAd(AdCredentials credentials);

    @Mapping(target = "imageUrl", expression =
            "java(URI.create(ad.getImages().stream().findFirst().orElse(\"\")))")

    @Mapping(target = "category", source = "category.id")
    @Mapping(target = "compares", ignore = true)
    AdComparesDto toComparesDto(Ad ad);

    @AfterMapping
    default void updateOrAddAdParameter(PatchAd patchAd, @MappingTarget Ad ad) {
        var dtoParameters = patchAd.getAdParameters();
        var parameters = ad.getParameters();

        if (dtoParameters == null) {
            return;
        }
        if (parameters == null) {
            ad.setParameters(dtoParameters.stream().map(dto -> {
                var adParameter = toAdParameterFromDto(dto);
                adParameter.setAd(ad);
                return adParameter;
            }).collect(Collectors.toSet()));
            return;
        }
        Set<AdParameter> toRemove = new HashSet<>(parameters);
        for (BasicUserParameter dto : dtoParameters) {
            boolean found = false;
            for (AdParameter parameter : parameters) {
                if (Objects.equals(dto.getId(), parameter.getId())) {
                    updateAdParameterFromDto(dto, parameter);
                    toRemove.remove(parameter);
                    found = true;
                    break;
                }
            }
            if (!found) {
                AdParameter adParameter = toAdParameterFromDto(dto);
                adParameter.setAd(ad);
                parameters.add(adParameter);
            }
        }
        parameters.removeAll(toRemove);
    }
}
