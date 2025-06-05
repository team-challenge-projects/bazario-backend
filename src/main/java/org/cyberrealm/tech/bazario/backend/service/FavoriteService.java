package org.cyberrealm.tech.bazario.backend.service;

import java.util.Map;
import org.cyberrealm.tech.bazario.backend.dto.AdResponseDto;
import org.springframework.data.domain.Page;

public interface FavoriteService {
    void add(Long adId);

    void delete(Long adId);

    Page<AdResponseDto> getAll(Map<String, String> filters);
}
