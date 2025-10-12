package org.cyberrealm.tech.bazario.backend.service;

import org.cyberrealm.tech.bazario.backend.dto.CategoryItemRequest;

public interface CategoryTypeItemService {
    Long create(CategoryItemRequest dto);

    void update(Long id, CategoryItemRequest dto);

    void delete(Long id);
}
