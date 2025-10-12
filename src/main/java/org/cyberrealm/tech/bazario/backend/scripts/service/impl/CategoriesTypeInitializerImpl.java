package org.cyberrealm.tech.bazario.backend.scripts.service.impl;

import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.script.ParameterCredentials;
import org.cyberrealm.tech.bazario.backend.model.Category;
import org.cyberrealm.tech.bazario.backend.model.CategoryTypeAdParameter;
import org.cyberrealm.tech.bazario.backend.model.TypeAdParameter;
import org.cyberrealm.tech.bazario.backend.repository.CategoryTypeAdParameterRepository;
import org.cyberrealm.tech.bazario.backend.scripts.service.CategoriesTypeInitializer;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoriesTypeInitializerImpl implements CategoriesTypeInitializer {
    private final CategoryTypeAdParameterRepository repository;

    @Override
    public List<CategoryTypeAdParameter> get(
            List<ParameterCredentials> categoryTypeItems,
            List<Category> categories, List<TypeAdParameter> adTypes) {
        Specification<CategoryTypeAdParameter> spec = (
                root, query, cb) ->
                categoryTypeItems.stream().map(item -> {
                    var categoryPredicate = cb.equal(root.get("category").get("id"),
                            categories.get(item.getOwnerId()).getId());
                    var typePredicate = cb.equal(root.get("type").get("id"),
                            adTypes.get(item.getTypeParameter()).getId());
                    var valuePredicate = cb.equal(root.get("name"), item.getParameterValue());
                    return cb.and(categoryPredicate, typePredicate, valuePredicate);
                }).reduce(cb::or).orElse(cb.disjunction());
        var existsParameters = repository.findAll(spec);

        var notExistsCredentials = categoryTypeItems.stream().filter(cred ->
                        existsParameters.stream().noneMatch(param ->
                                param.getCategory().getId().equals(categories.get(
                                        cred.getOwnerId()).getId())
                                        && param.getType().getId().equals(adTypes.get(
                                        cred.getTypeParameter()).getId())))
                .toList();
        if (!notExistsCredentials.isEmpty()) {
            return Stream.of(repository.saveAll(
                            notExistsCredentials.stream().map(item -> {
                                var newItem = new CategoryTypeAdParameter();
                                newItem.setCategory(categories.get(item.getOwnerId()));
                                newItem.setType(adTypes.get(item.getTypeParameter()));
                                newItem.setName(item.getParameterValue());
                                return newItem;
                            }).toList()),
                    existsParameters).flatMap(List::stream).toList();

        }
        return existsParameters;
    }
}
