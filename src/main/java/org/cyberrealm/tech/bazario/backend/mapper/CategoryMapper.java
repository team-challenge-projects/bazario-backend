package org.cyberrealm.tech.bazario.backend.mapper;

import org.cyberrealm.tech.bazario.backend.config.MapperConfig;
import org.cyberrealm.tech.bazario.backend.dto.CategoryResponseDto;
import org.cyberrealm.tech.bazario.backend.model.Category;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    CategoryResponseDto toCategoryDto(Category category);
}
