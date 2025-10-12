package org.cyberrealm.tech.bazario.backend.mapper;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.cyberrealm.tech.bazario.backend.config.MapperConfig;
import org.cyberrealm.tech.bazario.backend.dto.AdComparesDto;
import org.cyberrealm.tech.bazario.backend.dto.AdDto;
import org.cyberrealm.tech.bazario.backend.dto.AdDtoGet;
import org.cyberrealm.tech.bazario.backend.dto.AdResponseDto;
import org.cyberrealm.tech.bazario.backend.dto.BasicAdParameter;
import org.cyberrealm.tech.bazario.backend.dto.PatchAd;
import org.cyberrealm.tech.bazario.backend.dto.script.AdCredentials;
import org.cyberrealm.tech.bazario.backend.model.Ad;
import org.cyberrealm.tech.bazario.backend.model.AdParameter;
import org.cyberrealm.tech.bazario.backend.model.CategoryTypeAdParameter;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = MapperConfig.class)
public interface AdMapper {
    @Mapping(target = "cityCoordinate", expression =
            "java(ad.getCityCoordinate() != null ? ad.getCityCoordinate().toText() : \"null\")")
    @Mapping(target = "distance", source = "distance")
    @Mapping(target = "adParameters", source = "parameters")
    AdDtoGet toDtoGet(Ad ad, double distance, List<BasicAdParameter> parameters);

    @Mapping(target = "cityCoordinate", expression =
            "java(ad.getCityCoordinate() != null ? ad.getCityCoordinate().toText() : \"null\")")
    @Mapping(target = "distance", source = "distance")
    AdDto toDto(Ad ad, double distance);

    @Mapping(target = "imageUrl", expression =
            "java(URI.create(ad.getImages().stream().findFirst().orElse(\"\")))")

    @Mapping(target = "category", source = "category.id")
    @Mapping(target = "distance", ignore = true)
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
    @Mapping(target = "distance", ignore = true)
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
                var adParameter = new AdParameter();
                var categoryType = new CategoryTypeAdParameter();
                categoryType.setId(dto);
                adParameter.setParameter(categoryType);
                adParameter.setAd(ad);
                return adParameter;
            }).collect(Collectors.toSet()));
            return;
        }
        List<AdParameter> toRemove = new ArrayList<>(parameters);
        List<Long> toUpdate = new ArrayList<>(dtoParameters);
        for (Long dto : dtoParameters) {
            for (AdParameter parameter : parameters) {
                if (Objects.equals(dto, parameter.getParameter().getId())) {
                    toRemove.remove(parameter);
                    toUpdate.remove(dto);
                }
            }
        }
        if (!toUpdate.isEmpty()) {
            var sizeRemove = toRemove.size();
            for (int i = 0; i < toUpdate.size(); i++) {
                if (i < sizeRemove) {
                    var categoryType = new CategoryTypeAdParameter();
                    categoryType.setId(toUpdate.get(i));
                    toRemove.remove(i).setParameter(categoryType);
                } else {
                    var adParameter = new AdParameter();
                    adParameter.setAd(ad);
                    var categoryParameter = new CategoryTypeAdParameter();
                    categoryParameter.setId(toUpdate.get(i));
                    adParameter.setParameter(categoryParameter);
                    parameters.add(adParameter);
                }
            }
        }
        if (!toRemove.isEmpty()) {
            toRemove.forEach(parameters::remove);
        }
    }
}
