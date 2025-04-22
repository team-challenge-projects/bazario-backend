package org.cyberrealm.tech.bazario.backend.repository;

import java.util.Optional;
import org.cyberrealm.tech.bazario.backend.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    String CATEGORY_WITH_PARAMETERS = """
            SELECT c FROM Category c
             LEFT JOIN FETCH c.adParameters
             LEFT JOIN FETCH c.userParameters
             WHERE c.id = :id""";

    @Query(CATEGORY_WITH_PARAMETERS)
    Optional<Category> findByIdWithParameters(@Param("id") Long id);
}
