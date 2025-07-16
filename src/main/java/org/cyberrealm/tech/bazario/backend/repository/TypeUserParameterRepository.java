package org.cyberrealm.tech.bazario.backend.repository;

import java.util.List;
import org.cyberrealm.tech.bazario.backend.model.TypeUserParameter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeUserParameterRepository extends JpaRepository<TypeUserParameter, Long> {
    List<TypeUserParameter> findByNameIn(List<String> list);
}
