package org.cyberrealm.tech.bazario.backend.service;

import java.util.Map;
import org.cyberrealm.tech.bazario.backend.dto.AdDto;
import org.cyberrealm.tech.bazario.backend.dto.AdLeaderBoardDto;
import org.cyberrealm.tech.bazario.backend.dto.PatchAd;
import org.springframework.data.domain.Page;

public interface AdService {
    /**
     * Find ad by ad id and return public information
     *
     * @author Andrey Sitarskiy
     * @param id Ad id
     * @return Public information about ad
     */
    AdDto findById(Long id);

    /**
     * Delete ad by ad id
     *
     * @author Andrey Sitarskiy
     * @param id Ad id
     */
    void deleteById(Long id);

    /**
     * A user can have only one ad with the status NEW.
     * Needed to bind files from a remote service to the announcement.
     * If user is not owned ad with status NEW, create and get it.
     * If user is owned ad with status NEW, get it.
     *
     * @author Andrey Sitarskiy
     */
    AdDto createOrGet();

    /**
     * Patch ad by ad id and dto
     *
     * @author Andrey Sitarskiy
     * @param id Ad id
     * @param patchAd Dto for patch ad
     */
    void patchById(Long id, PatchAd patchAd);

    /**
     * The number of ads depends on the size of the cache.
     * When exceeded, ads with a lower score will be removed from the cache
     * when adding new ones, except for new ads with a trial period.
     * Get page with ad and score by filter.
     * Filter contains pagination and/or min and max score
     *
     * @author Andrey Sitarskiy
     * @param filters Name parameter and value
     * @return Page with dto contains ad fields and score
     */
    Page<AdLeaderBoardDto> getLeaderBoard(Map<String, String> filters);

}
