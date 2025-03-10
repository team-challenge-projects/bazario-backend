package org.cyberrealm.tech.bazario.backend.repository;

import java.util.Optional;
import org.cyberrealm.tech.bazario.backend.model.RefreshToken;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    boolean existsByUser(User user);

    void deleteByUser(User user);
}
