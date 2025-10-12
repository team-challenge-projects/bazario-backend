package org.cyberrealm.tech.bazario.backend.mapper;

import org.cyberrealm.tech.bazario.backend.config.MapperConfig;
import org.cyberrealm.tech.bazario.backend.dto.BasicAdParameter;
import org.cyberrealm.tech.bazario.backend.dto.CategoryItemRequest;
import org.cyberrealm.tech.bazario.backend.model.Category;
import org.cyberrealm.tech.bazario.backend.model.CategoryTypeAdParameter;
import org.cyberrealm.tech.bazario.backend.model.TypeAdParameter;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = MapperConfig.class)
public interface CategoryTypeAdParameterMapper {
    @Mapping(target = "typeItemId", source = "typeItem.id")
    @Mapping(target = "typeItemValue", source = "typeItem.name")
    @Mapping(target = "typeId", source = "typeItem.type.id")
    @Mapping(target = "typeValue", source = "typeItem.type.name")
    BasicAdParameter toBasicParameter(CategoryTypeAdParameter typeItem);

    @Mapping(target = "category", source = "categoryId", qualifiedByName = "mapCategory")
    @Mapping(target = "type", source = "typeAdParameterId", qualifiedByName = "mapTypeItem")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "adParameters", ignore = true)
    CategoryTypeAdParameter toCategoryTypeAdParameter(CategoryItemRequest dto);

    @Mapping(target = "category", source = "categoryId", qualifiedByName = "mapCategory")
    @Mapping(target = "type", source = "typeAdParameterId", qualifiedByName = "mapTypeItem")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "adParameters", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCategoryTypeAdParameter(
            CategoryItemRequest dto, @MappingTarget CategoryTypeAdParameter entity);

    @Named("mapCategory")
    default Category mapCategory(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        Category category = new Category();
        category.setId(categoryId);
        return category;
    }

    @Named("mapTypeItem")
    default TypeAdParameter mapTypeItem(Long id) {
        if (id == null) {
            return null;
        }
        TypeAdParameter item = new TypeAdParameter();
        item.setId(id);
        return item;
    }
}
