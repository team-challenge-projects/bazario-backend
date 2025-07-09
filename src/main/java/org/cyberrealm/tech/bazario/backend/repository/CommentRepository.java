package org.cyberrealm.tech.bazario.backend.repository;

import java.util.List;
import org.cyberrealm.tech.bazario.backend.dto.user.UserScore;
import org.cyberrealm.tech.bazario.backend.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Review, Long> {

    String FIND_USER_IDS_WITH_AVE_RATING_BETWEEN = """
            SELECT DISTINCT r.evaluated.id FROM Review r
             GROUP BY r.evaluated.id HAVING AVG(r.rating)
             BETWEEN :from AND :to""";
    String GET_AVG_RATING_BY_USER_IDS = """
            SELECT new org.cyberrealm.tech.bazario.backend.dto.user.UserScore(
             r.evaluated.id, AVG(r.rating)) FROM Review r
             WHERE r.evaluated.id IN (:ids) GROUP BY r.evaluated.id
            """;

    boolean existsByEvaluatorIdAndEvaluatedId(Long currentUserId, Long userId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.evaluated.id = :userId")
    Double findAverageRatingByEvaluatedId(@Param("userId") Long userId);

    @Query(FIND_USER_IDS_WITH_AVE_RATING_BETWEEN)
    List<Long> findUserIdsWithAverageRatingBetween(@Param("from") int from, @Param("to") int to);

    @Query(GET_AVG_RATING_BY_USER_IDS)
    List<UserScore> getAvgRatingByUserIds(@Param("ids") List<Long> ids);

    Page<Review> findByEvaluated_Id(Long id, Pageable pageable);
}
