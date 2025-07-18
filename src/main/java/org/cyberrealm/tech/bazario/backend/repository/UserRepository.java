package org.cyberrealm.tech.bazario.backend.repository;

import java.util.List;
import java.util.Optional;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    String USER_WITH_PARAMETER = """
            SELECT u FROM User u LEFT JOIN FETCH u.parameters
             LEFT JOIN FETCH u.parameters.parameter
             WHERE u.id = :userId""";

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByRole(Role role);

    List<User> findByEmailIn(List<String> emails);

    @Query(USER_WITH_PARAMETER)
    Optional<User> findByIdWithParameters(@Param("userId") Long userId);

    long countByRole(Role role);

    List<User> findByCityNameIn(List<String> names);
}
