package org.cyberrealm.tech.bazario.backend.scripts.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.script.CategoryCredentials;
import org.cyberrealm.tech.bazario.backend.mapper.CategoryMapper;
import org.cyberrealm.tech.bazario.backend.model.Category;
import org.cyberrealm.tech.bazario.backend.model.TypeAdParameter;
import org.cyberrealm.tech.bazario.backend.repository.CategoryRepository;
import org.cyberrealm.tech.bazario.backend.scripts.service.CategoryInitializer;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryInitializerImpl implements CategoryInitializer {
    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    @Override
    public Category getCategory(CategoryCredentials credentials, List<TypeAdParameter> parameters) {
        return repository.findByName(credentials.getName())
                .orElseGet(() -> getNewCategory(credentials, parameters));
    }

    @Override
    public List<Category> getCategories(List<CategoryCredentials> credentials,
                                        List<TypeAdParameter> parameters) {
        var existsCategories = repository.findByNameIn(credentials.stream()
                .map(CategoryCredentials::getName).toList());
        var notExistsCredentials = notExistsCredentials(credentials, existsCategories);

        if (notExistsCredentials.isEmpty()) {
            return existsCategories;
        }

        var notExistsCategories = createCategory(notExistsCredentials, parameters);
        return Stream.of(existsCategories, notExistsCategories)
                .flatMap(Collection::stream).toList();
    }

    private List<Category> createCategory(List<CategoryCredentials> credentials,
                                          List<TypeAdParameter> parameters) {
        var categories = credentials.stream().map(dto -> {
            var category = mapper.toCategory(dto);
            category.setAdParameters(dto.getTypeAdParameters().stream()
                    .map(parameters::get).collect(Collectors.toSet()));
            return category;
        }).toList();
        return repository.saveAll(categories);
    }

    private List<CategoryCredentials> notExistsCredentials(List<CategoryCredentials> credentials,
                                                           List<Category> categories) {
        var namesCategories = categories.stream().map(Category::getName).toList();
        return credentials.stream().filter(dto ->
                !namesCategories.contains(dto.getName())).toList();
    }

    private Category getNewCategory(CategoryCredentials credentials,
                                    List<TypeAdParameter> parameters) {
        Category category = new Category();
        category.setName(credentials.getName());
        category.setImage(credentials.getImage());
        category.setAdParameters(credentials.getTypeAdParameters().stream()
                .map(parameters::get).collect(Collectors.toSet()));
        return repository.save(category);
    }
}
