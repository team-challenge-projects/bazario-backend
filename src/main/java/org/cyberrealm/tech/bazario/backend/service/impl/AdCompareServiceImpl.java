package org.cyberrealm.tech.bazario.backend.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.PageCompareAd;
import org.cyberrealm.tech.bazario.backend.mapper.AdMapper;
import org.cyberrealm.tech.bazario.backend.repository.AdRepository;
import org.cyberrealm.tech.bazario.backend.repository.CommentRepository;
import org.cyberrealm.tech.bazario.backend.service.AdCompareService;
import org.cyberrealm.tech.bazario.backend.service.AuthenticationUserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdCompareServiceImpl implements AdCompareService {
    private final AdRepository adRepository;
    private final AdMapper adMapper;
    private final AuthenticationUserService authService;
    private final CommentRepository commentRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public PageCompareAd compares(List<Long> ids) {
        var ads = adRepository.findByIdIn(ids);
        var currentUser = authService.getCurrentUser();
        var citiesName = ads.stream().map(ad -> ad.getUser().getCityName())
                .distinct().toList();

        return null;
    }
}
