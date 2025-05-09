package org.cyberrealm.tech.bazario.backend.scripts.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.script.CategoryCredentials;
import org.cyberrealm.tech.bazario.backend.model.Category;
import org.cyberrealm.tech.bazario.backend.model.TypeAdParameter;
import org.cyberrealm.tech.bazario.backend.repository.CategoryRepository;
import org.cyberrealm.tech.bazario.backend.scripts.service.CategoryInitializer;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryInitializerImpl implements CategoryInitializer {
    private final CategoryRepository repository;

    @Override
    public Category getCategory(CategoryCredentials credentials, List<TypeAdParameter> parameters) {
        return repository.findByName(credentials.getName())
                .orElseGet(() -> getNewCategory(credentials, parameters));
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
