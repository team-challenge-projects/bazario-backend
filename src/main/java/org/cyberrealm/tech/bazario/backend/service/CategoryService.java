package org.cyberrealm.tech.bazario.backend.service;

import java.util.List;
import org.cyberrealm.tech.bazario.backend.dto.CategoryDto;
import org.cyberrealm.tech.bazario.backend.dto.CategoryRequestDto;
import org.cyberrealm.tech.bazario.backend.dto.CategoryResponseDto;
import org.cyberrealm.tech.bazario.backend.model.Category;

public interface CategoryService {
    /**
     * Create category
     *
     * @author Andrey Sitarskiy
     * @param categoryRequestDto Dto to create a category
     * @return category id
     */
    Long add(CategoryRequestDto categoryRequestDto);

    /**
     * Change category by category id
     *
     * @author Andrey Sitarskiy
     * @param categoryRequestDto Dto to change the category
     * @param id Category id
     */
    void put(Long id, CategoryRequestDto categoryRequestDto);

    /**
     * Delete category by category id
     *
     * @author Andrey Sitarskiy
     * @param id Category id
     */
    void delete(Long id);

    /**
     * Get category by category id with ad parameter and user parameter
     * associated with this category
     *
     * @author Andrey Sitarskiy
     * @param id Category id
     */
    CategoryResponseDto getCategoryWithParameters(Long id);

    /**
     * Get all categories
     *
     * @author Andrey Sitarskiy
     * @return All ad dto
     */
    List<CategoryDto> getAll();

    /**
     * Get category for add image
     *
     * @author Andrey Sitarskiy
     * @return entity
     */
    Category get(Long id);

    /**
     * Save entity with image
     *
     * @author Andrey Sitarskiy
     * @param category Entity with image
     */
    void save(Category category);
}
