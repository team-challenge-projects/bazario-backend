package org.cyberrealm.tech.bazario.backend.scripts.service;

import java.util.List;
import org.cyberrealm.tech.bazario.backend.dto.script.ParameterCredentials;
import org.cyberrealm.tech.bazario.backend.model.Category;
import org.cyberrealm.tech.bazario.backend.model.CategoryTypeAdParameter;
import org.cyberrealm.tech.bazario.backend.model.TypeAdParameter;

public interface CategoriesTypeInitializer {
    List<CategoryTypeAdParameter> get(
            List<ParameterCredentials> categoryTypeItems,
            List<Category> categories, List<TypeAdParameter> adTypes);
}
