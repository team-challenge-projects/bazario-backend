package org.cyberrealm.tech.bazario.backend.service.impl;

import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.CategoryDto;
import org.cyberrealm.tech.bazario.backend.dto.CategoryRequestDto;
import org.cyberrealm.tech.bazario.backend.dto.CategoryResponseDto;
import org.cyberrealm.tech.bazario.backend.exception.custom.EntityNotFoundException;
import org.cyberrealm.tech.bazario.backend.mapper.CategoryMapper;
import org.cyberrealm.tech.bazario.backend.model.Category;
import org.cyberrealm.tech.bazario.backend.repository.CategoryRepository;
import org.cyberrealm.tech.bazario.backend.repository.TypeAdParameterRepository;
import org.cyberrealm.tech.bazario.backend.repository.TypeUserParameterRepository;
import org.cyberrealm.tech.bazario.backend.service.CategoryService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final TypeAdParameterRepository adParamRepository;
    private final TypeUserParameterRepository userParamRepository;

    @Override
    public Long add(CategoryRequestDto dto) {
        var adParameters = adParamRepository.findAllById(
                dto.getAdParameterIds());
        var userParameters = userParamRepository.findAllById(
                dto.getUserParameterIds());
        Category category = new Category();
        category.setName(dto.getName());
        category.setAdParameters(new HashSet<>(adParameters));
        category.setUserParameters(new HashSet<>(userParameters));
        return categoryRepository.save(category).getId();
    }

    @Override
    public void put(Long id, CategoryRequestDto dto) {
        var adParameters = adParamRepository.findAllById(
                dto.getAdParameterIds());
        var userParameters = userParamRepository.findAllById(
                dto.getUserParameterIds());
        Category category = categoryRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Not found catalog with id :" + id));
        category.setName(dto.getName());
        category.setAdParameters(new HashSet<>(adParameters));
        category.setUserParameters(new HashSet<>(userParameters));
        categoryRepository.save(category);
    }

    @Override
    public void delete(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Not found catalog with id :" + id));
        categoryRepository.delete(category);
    }

    @Override
    public CategoryResponseDto getCategoryWithParameters(Long id) {
        var category = categoryRepository.findByIdWithParameters(id).orElseThrow(() ->
                new EntityNotFoundException("Category with id %s is not found"
                        .formatted(id)));
        return categoryMapper.toCategoryDto(category);
    }

    @Override
    public List<CategoryDto> getAll() {
        return categoryRepository.findAll().stream().map(category ->
                new CategoryDto().id(category.getId()).name(category.getName()))
                .toList();
    }
}
