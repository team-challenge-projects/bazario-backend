package org.cyberrealm.tech.bazario.backend.service.impl;

import java.util.HashSet;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.CategoryRequestDto;
import org.cyberrealm.tech.bazario.backend.exception.custom.EntityNotFoundException;
import org.cyberrealm.tech.bazario.backend.model.Category;
import org.cyberrealm.tech.bazario.backend.repository.CategoryRepository;
import org.cyberrealm.tech.bazario.backend.repository.TypeAdParameterRepository;
import org.cyberrealm.tech.bazario.backend.service.CategoryService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final TypeAdParameterRepository parameterRepository;

    @Override
    public Long add(CategoryRequestDto categoryRequestDto) {
        var parameters = parameterRepository.findAllById(
                categoryRequestDto.getAdParameterIds());
        Category category = new Category();
        category.setName(categoryRequestDto.getName());
        category.setAdParameters(new HashSet<>(parameters));
        return categoryRepository.save(category).getId();
    }

    @Override
    public void put(Long id, CategoryRequestDto categoryRequestDto) {
        var parameters = parameterRepository.findAllById(
                categoryRequestDto.getAdParameterIds());
        Category category = categoryRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Not found catalog with id :" + id));
        category.setName(categoryRequestDto.getName());
        category.setAdParameters(new HashSet<>(parameters));
        categoryRepository.save(category);
    }

    @Override
    public void delete(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Not found catalog with id :" + id));
        categoryRepository.delete(category);
    }
}
