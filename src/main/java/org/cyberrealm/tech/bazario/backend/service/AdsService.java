package org.cyberrealm.tech.bazario.backend.service;

import java.util.Map;
import org.cyberrealm.tech.bazario.backend.dto.AdResponseDto;
import org.springframework.data.domain.Page;

public interface AdsService {
    Page<AdResponseDto> findAll(Map<String, String> filters);
}
