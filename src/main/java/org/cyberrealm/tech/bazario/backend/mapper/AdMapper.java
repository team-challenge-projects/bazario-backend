package org.cyberrealm.tech.bazario.backend.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import org.cyberrealm.tech.bazario.backend.config.MapperConfig;
import org.cyberrealm.tech.bazario.backend.dto.ad.AdDto;
import org.cyberrealm.tech.bazario.backend.dto.ad.CreateAdRequestDto;
import org.cyberrealm.tech.bazario.backend.model.Ad;
import org.cyberrealm.tech.bazario.backend.model.Category;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface AdMapper {
    AdDto toDto(Ad ad);

    Ad toEntity(CreateAdRequestDto requestDto);

    @AfterMapping
    default void setCategoryIds(@MappingTarget AdDto adDto, Ad ad) {
        Set<Long> categoryIds = ad.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toSet());
        adDto.setCategoryIds(categoryIds);
    }

}
