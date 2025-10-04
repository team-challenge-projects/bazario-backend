package org.cyberrealm.tech.bazario.backend.service;

import java.util.Map;
import org.cyberrealm.tech.bazario.backend.dto.AdResponseDto;
import org.springframework.data.domain.Page;

public interface AdsService {
    /**
     * Filtering of ads by fields, by related ad parameters, by fields
     * of the related user and its calculated parameters: rating, distance,
     * user parameters.
     *
     * @author Andrey Sitarskiy
     * @param filters Name parameter and value
     * @return Page ad dto
     */
    Page<AdResponseDto> findAll(Map<String, String> filters);

    /**
     * Filtering of ads by fields, by related ad parameters, by fields
     * of the related user and its calculated parameters: rating, distance,
     * user parameters.
     *
     * @author Andrey Sitarskiy
     * @param filters Name parameter and value
     * @return Page ad dto
     */
    Page<AdResponseDto> findAllForUser(Map<String, String> filters);
}
