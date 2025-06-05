package org.cyberrealm.tech.bazario.backend.repository;

import java.util.Optional;
import org.cyberrealm.tech.bazario.backend.model.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByUser_IdAndAd_Id(Long userId, Long adId);

    @EntityGraph(attributePaths = {"ad", "ad.images"})
    Page<Favorite> findByUser_Id(Long userId, Pageable pageable);

}
