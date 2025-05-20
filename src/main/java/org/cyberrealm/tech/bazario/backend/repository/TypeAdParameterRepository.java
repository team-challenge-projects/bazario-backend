package org.cyberrealm.tech.bazario.backend.repository;

import java.util.List;
import java.util.Optional;
import org.cyberrealm.tech.bazario.backend.model.TypeAdParameter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeAdParameterRepository extends JpaRepository<TypeAdParameter, Long> {
    Optional<TypeAdParameter> findByName(String name);

    List<TypeAdParameter> findByNameIn(List<String> parameters);
}
