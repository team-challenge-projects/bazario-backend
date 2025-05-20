package org.cyberrealm.tech.bazario.backend.scripts.service;

import java.util.List;
import org.cyberrealm.tech.bazario.backend.dto.script.CategoryCredentials;
import org.cyberrealm.tech.bazario.backend.model.Category;
import org.cyberrealm.tech.bazario.backend.model.TypeAdParameter;

public interface CategoryInitializer {
    Category getCategory(CategoryCredentials credentials, List<TypeAdParameter> parameters);

    List<Category> getCategories(List<CategoryCredentials> credentials,
                                 List<TypeAdParameter> parameters);
}
