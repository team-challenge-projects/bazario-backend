package org.cyberrealm.tech.bazario.backend.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.cyberrealm.tech.bazario.backend.dto.CommentDto;
import org.cyberrealm.tech.bazario.backend.mapper.CommentMapper;
import org.cyberrealm.tech.bazario.backend.model.Review;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.repository.CommentRepository;
import org.cyberrealm.tech.bazario.backend.repository.UserRepository;
import org.cyberrealm.tech.bazario.backend.service.impl.CommentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    public static final long ONE_ID = 1L;
    public static final long TWO_ID = 2L;
    @Mock
    private AuthenticationUserService authUserService;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private PageableService pageableService;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    void add() {
        var dto = new CommentDto().description("Test").rating(1);
        var review = new Review();
        review.setReviewText(dto.getDescription());
        review.setRating(dto.getRating());
        when(commentMapper.toReview(dto)).thenReturn(review);

        var currentUser = new User();
        currentUser.setId(ONE_ID);
        when(authUserService.getCurrentUser()).thenReturn(currentUser);

        var user = new User();
        user.setId(TWO_ID);
        when(userRepository.findById(TWO_ID)).thenReturn(Optional.of(user));
        when(commentRepository.existsByEvaluatorIdAndEvaluatedId(ONE_ID, TWO_ID))
                .thenReturn(false);

        review.setId(ONE_ID);
        when(commentRepository.save(any(Review.class))).thenReturn(review);

        assertEquals(ONE_ID, commentService.add(TWO_ID, dto));
    }

    @Test
    void put() {
        var dto = new CommentDto().description("Test").rating(1);
        var currentUser = new User();
        currentUser.setId(ONE_ID);
        var review = new Review();
        review.setReviewText(dto.getDescription());
        review.setRating(dto.getRating());
        review.setEvaluator(currentUser);
        when(commentRepository.findById(ONE_ID)).thenReturn(Optional.of(review));
        when(authUserService.isAdmin()).thenReturn(false);
        when(authUserService.getCurrentUser()).thenReturn(currentUser);

        commentService.put(ONE_ID, dto);

        verify(commentMapper).updateReviewFromDto(dto, review);
        verify(commentRepository).save(review);
    }

    @Test
    void delete() {
        var currentUser = new User();
        currentUser.setId(ONE_ID);
        var review = new Review();
        review.setEvaluator(currentUser);
        when(commentRepository.findById(ONE_ID)).thenReturn(Optional.of(review));
        when(authUserService.isAdmin()).thenReturn(false);
        when(authUserService.getCurrentUser()).thenReturn(currentUser);

        commentService.delete(ONE_ID);

        verify(commentRepository).delete(review);
    }

    @Test
    void getTotalRating() {
        commentService.getTotalRating(ONE_ID);
        verify(commentRepository).findAverageRatingByEvaluatedId(ONE_ID);
    }

    @Test
    void getByUserId() {
        var filter = Map.of("Test", "Test");
        var review = new Review();
        review.setReviewText("Test");
        review.setRating(1);
        var pageable = PageRequest.of(0, 16);
        var page = new PageImpl<>(List.of(review), pageable, ONE_ID);
        when(pageableService.get(filter)).thenReturn(pageable);
        when(commentRepository.findByEvaluated_Id(ONE_ID, pageable)).thenReturn(page);
        var dto = new CommentDto().description("Test").rating(1);
        when(commentMapper.toDto(review)).thenReturn(dto);

        var newPage = commentService.getByUserId(ONE_ID, filter);
        assertAll(
                () -> assertEquals(page.getTotalElements(), newPage.getTotalElements()),
                () -> assertEquals(page.getPageable(), newPage.getPageable()),
                () -> assertEquals(dto, newPage.getContent().get(0))
        );
    }

    @Test
    void getUserIdsByRangeRating() {
        when(commentRepository.findUserIdsWithAverageRatingBetween(1, 10))
                .thenReturn(List.of(ONE_ID));
        assertEquals(ONE_ID, commentService.getUserIdsByRangeRating(1, 10).get(0));
    }
}
