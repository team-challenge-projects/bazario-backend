package org.cyberrealm.tech.bazario.backend.service;

import java.util.Map;
import org.cyberrealm.tech.bazario.backend.dto.AdResponseDto;
import org.springframework.data.domain.Page;

public interface FavoriteService {
    /**
     * Add ad to favorite list
     *
     * @author Andrey Sitarskiy
     * @param adId Ad id
     */
    void add(Long adId);

    /**
     * Delete ad from favorite list
     *
     * @author Andrey Sitarskiy
     * @param adId Ad id
     */
    void delete(Long adId);

    /**
     * Get ads from favorite list by filter.
     * Filter contains pagination
     *
     * @author Andrey Sitarskiy
     * @param filters Name parameter and value
     */
    Page<AdResponseDto> getAll(Map<String, String> filters);
}
