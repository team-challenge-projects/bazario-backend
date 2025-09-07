package org.cyberrealm.tech.bazario.backend.repository;

import jakarta.persistence.Tuple;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.cyberrealm.tech.bazario.backend.dto.AdStatus;
import org.cyberrealm.tech.bazario.backend.model.Ad;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AdRepository extends JpaRepository<Ad, Long>,
        JpaSpecificationExecutor<Ad> {

    String COUNT_GROUP_BY_USER = """
            SELECT ad.user.id as userId, COUNT(ad) as count FROM Ad ad
             WHERE ad.status = :status AND ad.user.id IN :userIds
             GROUP BY ad.user.id""";
    String EARLIEST_DATE = """
            SELECT ranked_ads.* FROM (SELECT ad.*,
             ROW_NUMBER() OVER (PARTITION BY ad.user_id ORDER BY ad.publication_date) AS rn
             FROM ads ad WHERE ad.status = :status) AS ranked_ads
             INNER JOIN UNNEST(CAST(:userIds AS BIGINT[]),
             CAST(:ranks AS INTEGER[])) AS pairs(user_id, rn)
             ON ranked_ads.user_id = pairs.user_id AND ranked_ads.rn = pairs.rn""";

    @EntityGraph(attributePaths = {"parameters", "images"})
    List<Ad> findByStatusAndUser(AdStatus statusAd, User user);

    @Query("SELECT ad FROM Ad ad LEFT JOIN FETCH ad.parameters WHERE ad.id = :id")
    Optional<Ad> findByIdWithParameters(@Param("id") Long id);

    boolean existsByTitleAndPrice(String title, BigDecimal price);

    @EntityGraph(attributePaths = {"images"})
    List<Ad> findByUser(User user);

    @Query("SELECT ad.images FROM Ad ad JOIN ad.images WHERE ad.id IN :adIds")
    List<String> findImageUrlsByAdIds(@Param("adIds") List<Long> adIds);

    @Query(COUNT_GROUP_BY_USER)
    List<Tuple> countGroupByUser(@Param("status") AdStatus status,
                                 @Param("userIds") List<Long> userIds);

    @Query(value = EARLIEST_DATE, nativeQuery = true)
    List<Ad> findByStatusAndEarliestDate(
            @Param("status") String adStatus, @Param("userIds") Long[] userIds,
            @Param("ranks") Integer[] ranks);
}
