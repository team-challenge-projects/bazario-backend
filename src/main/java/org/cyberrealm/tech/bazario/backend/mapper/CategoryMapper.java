package org.cyberrealm.tech.bazario.backend.mapper;

import org.cyberrealm.tech.bazario.backend.config.MapperConfig;
import org.cyberrealm.tech.bazario.backend.dto.CategoryResponseDto;
import org.cyberrealm.tech.bazario.backend.dto.script.CategoryCredentials;
import org.cyberrealm.tech.bazario.backend.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    CategoryResponseDto toCategoryDto(Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "adParameters", ignore = true)
    @Mapping(target = "userParameters", ignore = true)
    Category toCategory(CategoryCredentials credentials);

}
