package org.cyberrealm.tech.bazario.backend.service;

import java.util.List;
import org.cyberrealm.tech.bazario.backend.dto.CategoryDto;
import org.cyberrealm.tech.bazario.backend.dto.CategoryRequestDto;
import org.cyberrealm.tech.bazario.backend.dto.CategoryResponseDto;

public interface CategoryService {

    Long add(CategoryRequestDto categoryRequestDto);

    void put(Long id, CategoryRequestDto categoryRequestDto);

    void delete(Long id);

    CategoryResponseDto getCategoryWithParameters(Long id);

    List<CategoryDto> getAll();
}
