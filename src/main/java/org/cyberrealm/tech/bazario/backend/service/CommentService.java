package org.cyberrealm.tech.bazario.backend.service;

import java.util.List;
import java.util.Map;
import org.cyberrealm.tech.bazario.backend.dto.CommentDto;
import org.springframework.data.domain.Page;

public interface CommentService {
    /**
     * Add comment authorization user by other user id
     *
     * @author Andrey Sitarskiy
     * @param id Other user id
     * @param dto Dto to add comment
     * @return Comment id
     */
    Long add(Long id, CommentDto dto);

    /**
     * Change comment authorization user by other user id
     *
     * @author Andrey Sitarskiy
     * @param id Other user id
     * @param dto Dto to add comment
     */
    void put(Long id, CommentDto dto);

    /**
     * Delete comment authorization user by other user id
     *
     * @author Andrey Sitarskiy
     * @param id Other user id
     */
    void delete(Long id);

    /**
     * Get user rating by user id
     *
     * @author Andrey Sitarskiy
     * @param id User id
     * @return User rating
     */
    Double getTotalRating(Long id);

    /**
     * Get page with comments by filter.
     * Filter contains pagination.
     *
     * @author Andrey Sitarskiy
     * @param id User id
     * @param filters Name parameter and value
     */
    Page<CommentDto> getByUserId(Long id, Map<String, String> filters);

    /**
     * Get user ids by rating
     *
     * @author Andrey Sitarskiy
     * @param from Min rating
     * @param to Max rating
     */
    List<Long> getUserIdsByRangeRating(int from, int to);
}
