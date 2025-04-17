package org.cyberrealm.tech.bazario.backend.service;

import org.cyberrealm.tech.bazario.backend.dto.CategoryRequestDto;

public interface CategoryService {
    Long add(CategoryRequestDto categoryRequestDto);

    void put(Long id, CategoryRequestDto categoryRequestDto);

    void delete(Long id);
}
