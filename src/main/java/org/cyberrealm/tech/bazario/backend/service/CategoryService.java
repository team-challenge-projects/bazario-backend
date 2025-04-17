package org.cyberrealm.tech.bazario.backend.service;

import org.cyberrealm.tech.bazario.backend.dto.CategoryRequestDto;
import org.cyberrealm.tech.bazario.backend.model.Category;

public interface CategoryService {
    void add(CategoryRequestDto categoryRequestDto);

    void put(Long id, CategoryRequestDto categoryRequestDto);

    void delete(Long id);

    Category findFirst();
}
