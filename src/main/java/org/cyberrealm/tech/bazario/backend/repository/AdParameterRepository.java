package org.cyberrealm.tech.bazario.backend.repository;

import java.util.List;
import org.cyberrealm.tech.bazario.backend.model.AdParameter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AdParameterRepository extends JpaRepository<AdParameter, Long>,
        JpaSpecificationExecutor<AdParameter> {
    <T> List<T> findAll(Specification<AdParameter> spec, Class<T> projectionClass);
}
