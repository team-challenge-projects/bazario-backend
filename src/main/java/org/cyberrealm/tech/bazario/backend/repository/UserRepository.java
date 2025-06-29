package org.cyberrealm.tech.bazario.backend.repository;

import java.util.List;
import java.util.Optional;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByRole(Role role);

    List<User> findByEmailIn(List<String> emails);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.parameters WHERE u.id = :userId")
    Optional<User> findByIdWithParameters(@Param("userId") Long userId);

    long countByRole(Role role);

    List<User> findByCityNameIn(List<String> names);
}
