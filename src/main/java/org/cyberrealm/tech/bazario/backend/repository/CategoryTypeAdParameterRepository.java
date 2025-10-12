package org.cyberrealm.tech.bazario.backend.repository;

import org.cyberrealm.tech.bazario.backend.model.CategoryTypeAdParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CategoryTypeAdParameterRepository extends
        JpaRepository<CategoryTypeAdParameter, Long>,
        JpaSpecificationExecutor<CategoryTypeAdParameter> {
}
