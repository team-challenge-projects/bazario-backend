package org.cyberrealm.tech.bazario.backend.repository;

import java.util.List;
import org.cyberrealm.tech.bazario.backend.model.Ad;
import org.cyberrealm.tech.bazario.backend.model.AdParameter;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AdParameterRepository extends JpaRepository<AdParameter, Long>,
        JpaSpecificationExecutor<AdParameter> {

    @EntityGraph(attributePaths = {"parameter", "parameter.type"})
    List<AdParameter> findByAd(Ad ad);
}
