package org.cyberrealm.tech.bazario.backend.service;

import java.util.Map;
import java.util.Set;
import org.cyberrealm.tech.bazario.backend.model.Ad;
import org.springframework.data.redis.core.ZSetOperations;

public interface AccessAdService {
    Ad getProtectedAd(Long id);

    void save(Ad ad);

    boolean isNotAccessAd(Ad ad);

    Ad getPublicAd(Long id);

    Set<ZSetOperations.TypedTuple<Object>> getLeaderBoardContent(Map<String, String> filter);

    long getLeaderBoardCount(Map<String, String> filter);
}
