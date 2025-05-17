package org.cyberrealm.tech.bazario.backend.api.impl;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.api.CommentApiDelegate;
import org.cyberrealm.tech.bazario.backend.dto.CommentDto;
import org.cyberrealm.tech.bazario.backend.service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentApiDelegateImpl implements CommentApiDelegate {
    private final CommentService commentService;

    @Override
    public ResponseEntity<Long> addComment(Long id, CommentDto commentDto) {
        return ResponseEntity.ok(commentService.add(id, commentDto));
    }

    @Override
    public ResponseEntity<Void> deleteComment(Long id) {
        commentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Page> getComments(Long id, Map<String, String> filters) {
        return ResponseEntity.ok(commentService.getByUserId(id, filters));
    }

    @Override
    public ResponseEntity<Integer> getTotalRating(Long id) {
        return ResponseEntity.ok(commentService.getTotalRating(id));
    }

    @Override
    public ResponseEntity<Void> putComment(Long id, CommentDto commentDto) {
        commentService.put(id, commentDto);
        return ResponseEntity.noContent().build();
    }
}
