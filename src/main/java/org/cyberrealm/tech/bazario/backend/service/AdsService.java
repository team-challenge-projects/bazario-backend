package org.cyberrealm.tech.bazario.backend.service;

import org.cyberrealm.tech.bazario.backend.dto.AdResponseDto;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface AdsService {
    Page<AdResponseDto> findAll(Map<String, String> filters);
}
