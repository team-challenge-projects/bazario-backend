package org.cyberrealm.tech.bazario.backend.api.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import lombok.SneakyThrows;
import org.cyberrealm.tech.bazario.backend.AbstractIntegrationTest;
import org.cyberrealm.tech.bazario.backend.dto.CommentDto;
import org.cyberrealm.tech.bazario.backend.repository.CommentRepository;
import org.cyberrealm.tech.bazario.backend.repository.UserRepository;
import org.cyberrealm.tech.bazario.backend.service.AuthenticationUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class CommentApiDelegateImplTest extends AbstractIntegrationTest {
    private static final int RATING = 5;

    @MockitoBean
    private AuthenticationUserService authService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;

    @SneakyThrows
    @WithMockUser
    @Test
    void addComment() {
        var user = userRepository.findById(ID_TWO).orElseThrow();
        when(authService.getCurrentUser()).thenReturn(user);
        var dto = new CommentDto().rating((int) ID_ONE).description("Test");
        mockMvc.perform(post("/private/comment/user/" + ID_ONE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(ID_TWO)));
        var comment = commentRepository.findById(ID_TWO).orElseThrow();
        assertAll(
                () -> assertEquals(dto.getRating(), comment.getRating()),
                () -> assertEquals(dto.getDescription(), comment.getReviewText()),
                () -> assertEquals(ID_TWO, comment.getEvaluator().getId()),
                () -> assertEquals(ID_ONE, comment.getEvaluated().getId())
        );
    }

    @SneakyThrows
    @WithMockUser
    @Test
    void deleteComment() {
        var user = userRepository.findById(ID_ONE).orElseThrow();

        when(authService.isAdmin()).thenReturn(false);
        when(authService.getCurrentUser()).thenReturn(user);

        mockMvc.perform(delete("/private/comment/" + ID_ONE))
                .andExpect(status().isNoContent());
        assertThrows(NoSuchElementException.class, () ->
                commentRepository.findById(ID_ONE).orElseThrow());
    }

    @SneakyThrows
    @Test
    void getTotalRating() {
        mockMvc.perform(get("/public/comment/user/%d/totalRating"
                        .formatted(ID_TWO)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper
                        .writeValueAsString((double) ID_ONE)));
    }

    @SneakyThrows
    @WithMockUser
    @Test
    void putComment() {
        var user = userRepository.findById(ID_ONE).orElseThrow();
        when(authService.isAdmin()).thenReturn(false);
        when(authService.getCurrentUser()).thenReturn(user);

        var dto = new CommentDto().rating(RATING).description("New Test");

        var entity = commentRepository.findById(ID_ONE).orElseThrow();
        entityManager.clear();

        mockMvc.perform(put("/private/comment/" + ID_ONE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
        var newEntity = commentRepository.findById(ID_ONE).orElseThrow();
        assertAll(
                () -> assertNotEquals(entity.getRating(), newEntity.getRating()),
                () -> assertNotEquals(entity.getReviewText(), newEntity.getReviewText())
        );
    }

    @SneakyThrows
    @Test
    void getComments() {
        var dto = new CommentDto().rating((int) ID_ONE).description("Тест");
        var pageDto = new PageImpl<CommentDto>(List.of(dto),
                PageRequest.of(0, 16,
                        Sort.by("id").ascending()),
                1L);
        mockMvc.perform(get("/public/comment/user/" + ID_ONE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of())))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(pageDto)));
    }

}
