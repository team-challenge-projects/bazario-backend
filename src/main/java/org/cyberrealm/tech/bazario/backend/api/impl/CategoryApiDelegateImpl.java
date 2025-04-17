package org.cyberrealm.tech.bazario.backend.api.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.api.CategoryApiDelegate;
import org.cyberrealm.tech.bazario.backend.dto.AdParameterDto;
import org.cyberrealm.tech.bazario.backend.dto.CategoryRequestDto;
import org.cyberrealm.tech.bazario.backend.dto.CategoryResponseDto;
import org.cyberrealm.tech.bazario.backend.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryApiDelegateImpl implements CategoryApiDelegate {
    private final CategoryService categoryService;

    @Override
    public ResponseEntity<Long> addCategory(CategoryRequestDto categoryRequestDto) {
        return ResponseEntity.ok(categoryService.add(categoryRequestDto));
    }

    @Override
    public ResponseEntity<Void> deleteCategory(Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<String>> getCategories() {
        return CategoryApiDelegate.super.getCategories();
    }

    @Override
    public ResponseEntity<CategoryResponseDto> getCategory(Long id) {
        return CategoryApiDelegate.super.getCategory(id);
    }

    @Override
    public ResponseEntity<List<AdParameterDto>> getCategoryAdParameter(Long id) {
        return CategoryApiDelegate.super.getCategoryAdParameter(id);
    }

    @Override
    public ResponseEntity<Void> putCategory(Long id, CategoryRequestDto categoryRequestDto) {
        categoryService.put(id, categoryRequestDto);
        return ResponseEntity.noContent().build();
    }
}
