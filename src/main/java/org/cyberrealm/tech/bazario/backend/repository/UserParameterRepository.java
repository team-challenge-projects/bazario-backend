package org.cyberrealm.tech.bazario.backend.repository;

import java.util.List;
import org.cyberrealm.tech.bazario.backend.model.UserParameter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserParameterRepository extends JpaRepository<UserParameter, Long>,
        JpaSpecificationExecutor<UserParameter> {
    <T> List<T> findBy(Specification<UserParameter> spec, Class<T> projectionClass);
}
