package org.cyberrealm.tech.bazario.backend.service;

import java.util.Map;
import java.util.Set;
import org.cyberrealm.tech.bazario.backend.exception.custom.ForbiddenException;
import org.cyberrealm.tech.bazario.backend.model.Ad;
import org.springframework.data.redis.core.ZSetOperations;

public interface AccessAdService {
    /**
     * Used to change the ad.
     * It pulls the ad from the cache, and if it is not in the cache,
     * it pulls it from the database and puts it into the cache.
     * If the user does not have rights to the ad, an exception is thrown.
     *
     * @author Andrey Sitarskiy
     * @param id Ad identifier
     * @return Ad by id.
     * @exception ForbiddenException If the user does not have rights to the ad
     */
    Ad getProtectedAd(Long id);

    /**
     * It is used to save the announcement to the database after uploading
     * the files to the remote service and receiving the url.
     *
     * @author Andrey Sitarskiy
     * @param ad Ad with image url
     */
    void save(Ad ad);

    /**
     * Checks the user's right to this ad. The user must be the owner or
     * have the role of Admin or Root.
     *
     * @author Andrey Sitarskiy
     * @param ad Ad
     */
    boolean isNotAccessAd(Ad ad);

    /**
     * Used to return to the dto
     * It pulls the ad from the cache, and if it is not in the cache,
     * it pulls it from the database and puts it into the cache.
     * If ad status is not ACTIVE and the user does not have rights
     * to the ad, an exception is thrown.
     *
     * @author Andrey Sitarskiy
     * @param id Ad identifier
     * @return Ad by id.
     * @exception ForbiddenException If ad status is not ACTIVE and the user
     * does not have rights to the ad.
     */
    Ad getPublicAd(Long id);

    /**
     * Generates content for the board leader for the page by filters.
     * Filters can contain pagination and/or minimum, maximum score.
     *
     * @author Andrey Sitarskiy
     * @param filter Pagination and/or minimum, maximum score
     * @return Object with ad id and score
     */
    Set<ZSetOperations.TypedTuple<Object>> getLeaderBoardContent(Map<String, String> filter);

    /**
     * Generates total count for the board leader for the page by filters.
     * Filters can contain pagination and/or minimum, maximum score.
     *
     * @author Andrey Sitarskiy
     * @param filter Pagination and/or minimum, maximum score. Name param and value.
     * @return Total count
     */
    long getLeaderBoardCount(Map<String, String> filter);
}
