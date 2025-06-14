package org.cyberrealm.tech.bazario.backend.repository;

import org.cyberrealm.tech.bazario.backend.model.UserParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserParameterRepository extends JpaRepository<UserParameter, Long>,
        JpaSpecificationExecutor<UserParameter> {
}
