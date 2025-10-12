package org.cyberrealm.tech.bazario.backend.api.impl;

import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.api.CategoryItemOfAdParameterApiDelegate;
import org.cyberrealm.tech.bazario.backend.dto.CategoryItemRequest;
import org.cyberrealm.tech.bazario.backend.service.CategoryTypeItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryTypeItemApiDelegateImpl implements CategoryItemOfAdParameterApiDelegate {
    private final CategoryTypeItemService service;

    @Override
    public ResponseEntity<Long> createCategoryItemParameter(CategoryItemRequest dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @Override
    public ResponseEntity<Void> deleteCategoryItemParameter(Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> putCategoryItemParameter(
            Long id, CategoryItemRequest dto) {
        service.update(id, dto);
        return ResponseEntity.noContent().build();
    }
}
