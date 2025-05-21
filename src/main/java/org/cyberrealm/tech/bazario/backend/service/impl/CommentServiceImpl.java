package org.cyberrealm.tech.bazario.backend.service.impl;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.CommentDto;
import org.cyberrealm.tech.bazario.backend.exception.custom.EntityNotFoundException;
import org.cyberrealm.tech.bazario.backend.exception.custom.ForbiddenException;
import org.cyberrealm.tech.bazario.backend.mapper.CommentMapper;
import org.cyberrealm.tech.bazario.backend.model.Review;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.repository.CommentRepository;
import org.cyberrealm.tech.bazario.backend.repository.UserRepository;
import org.cyberrealm.tech.bazario.backend.service.AuthenticationUserService;
import org.cyberrealm.tech.bazario.backend.service.CommentService;
import org.cyberrealm.tech.bazario.backend.service.PageableService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final AuthenticationUserService authUserService;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final PageableService pageableService;
    private final UserRepository userRepository;

    @Override
    public Long add(Long id, CommentDto dto) {
        var review = commentMapper.toReview(dto);
        User currentUser = authUserService.getCurrentUser();
        User user = userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("User with id %d not found"
                        .formatted(id)));
        if (commentRepository.existsByEvaluatorIdAndEvaluatedId(
                currentUser.getId(), user.getId())) {
            throw new EntityNotFoundException("Comment by user with id %d is exists"
                    .formatted(id));
        }
        review.setEvaluator(currentUser);
        review.setEvaluated(user);
        return commentRepository.save(review).getId();
    }

    @Override
    public void put(Long id, CommentDto dto) {
        var review = getReview(id);
        commentMapper.updateReviewFromDto(dto, review);
        commentRepository.save(review);
    }

    @Override
    public void delete(Long id) {
        commentRepository.delete(getReview(id));
    }

    @Override
    public Integer getTotalRating(Long id) {
        return commentRepository.findAverageRatingByEvaluatedId(id).intValue();
    }

    @Override
    public Page<CommentDto> getByUserId(Long id, Map<String, String> filters) {
        return commentRepository.findAll(pageableService.get(filters))
                .map(commentMapper::toDto);
    }

    @Override
    public List<Long> getUserIdsByRangeRating(int from, int to) {
        return commentRepository.findUserIdsWithAverageRatingBetween(from, to);
    }

    private Review getReview(Long id) {
        var review = commentRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Review with id %d not found".formatted(id)));
        if (!authUserService.isAdmin() && !authUserService.getCurrentUser().getId()
                .equals(review.getEvaluator().getId())) {
            throw new ForbiddenException("User not access to review");
        }
        return review;
    }
}
