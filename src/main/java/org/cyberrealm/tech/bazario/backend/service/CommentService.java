package org.cyberrealm.tech.bazario.backend.service;

import java.util.List;
import java.util.Map;
import org.cyberrealm.tech.bazario.backend.dto.CommentDto;
import org.springframework.data.domain.Page;

public interface CommentService {
    Long add(Long id, CommentDto dto);

    void put(Long id, CommentDto dto);

    void delete(Long id);

    Double getTotalRating(Long id);

    Page<CommentDto> getByUserId(Long id, Map<String, String> filters);

    List<Long> getUserIdsByRangeRating(int from, int to);
}
