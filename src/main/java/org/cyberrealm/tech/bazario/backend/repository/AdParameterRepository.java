package org.cyberrealm.tech.bazario.backend.repository;

import org.cyberrealm.tech.bazario.backend.model.AdParameter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface AdParameterRepository extends JpaRepository<AdParameter, Long>,
        JpaSpecificationExecutor<AdParameter>{
    <T> List<T> findAll(Specification<AdParameter> spec, Class<T> projectionClass);
}
