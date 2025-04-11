package org.cyberrealm.tech.bazario.backend.repository;

import org.cyberrealm.tech.bazario.backend.dto.AdStatus;
import org.cyberrealm.tech.bazario.backend.model.Ad;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AdRepository extends JpaRepository<Ad, Long>, JpaSpecificationExecutor<Ad> {
    Optional<Ad> findByStatus(AdStatus statusAd);
}
