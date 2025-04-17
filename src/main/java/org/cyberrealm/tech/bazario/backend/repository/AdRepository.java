package org.cyberrealm.tech.bazario.backend.repository;

import java.util.Optional;
import org.cyberrealm.tech.bazario.backend.dto.AdStatus;
import org.cyberrealm.tech.bazario.backend.model.Ad;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AdRepository extends JpaRepository<Ad, Long>, JpaSpecificationExecutor<Ad> {
    Optional<Ad> findByStatusAndUser(AdStatus statusAd, User user);

    @Query("SELECT ad FROM Ad ad LEFT JOIN FETCH ad.parameters WHERE ad.id = :id")
    Optional<Ad> findByIdWithParameters(@Param("id") Long id);
}
