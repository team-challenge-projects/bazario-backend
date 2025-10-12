package org.cyberrealm.tech.bazario.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.CategoryItemRequest;
import org.cyberrealm.tech.bazario.backend.exception.custom.EntityNotFoundException;
import org.cyberrealm.tech.bazario.backend.mapper.CategoryTypeAdParameterMapper;
import org.cyberrealm.tech.bazario.backend.repository.CategoryTypeAdParameterRepository;
import org.cyberrealm.tech.bazario.backend.service.CategoryTypeItemService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryTypeItemServiceImpl implements CategoryTypeItemService {
    private final CategoryTypeAdParameterMapper mapper;
    private final CategoryTypeAdParameterRepository repository;

    @Override
    public Long create(CategoryItemRequest dto) {
        return repository.save(mapper.toCategoryTypeAdParameter(dto)).getId();
    }

    @Override
    public void update(Long id, CategoryItemRequest dto) {
        var item = repository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(
                        "Category ad parameter item with %s not found"
                                .formatted(id)));
        mapper.updateCategoryTypeAdParameter(dto, item);
        repository.save(item);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
