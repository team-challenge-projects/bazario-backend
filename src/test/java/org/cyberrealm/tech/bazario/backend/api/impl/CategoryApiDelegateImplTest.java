package org.cyberrealm.tech.bazario.backend.api.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.NoSuchElementException;
import lombok.SneakyThrows;
import org.cyberrealm.tech.bazario.backend.AbstractIntegrationTest;
import org.cyberrealm.tech.bazario.backend.dto.CategoryDto;
import org.cyberrealm.tech.bazario.backend.dto.CategoryRequestDto;
import org.cyberrealm.tech.bazario.backend.dto.CategoryResponseDto;
import org.cyberrealm.tech.bazario.backend.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

class CategoryApiDelegateImplTest extends AbstractIntegrationTest {
    private static final String AD_NAME = "Доставка тест";
    private static final String USER_NAME = "Тестовий тип";
    private static final String name = "New Test";
    private static final CategoryRequestDto dto = new CategoryRequestDto();

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryApiDelegateImpl controller;

    @BeforeAll
    static void beforeAll() {
        dto.setName(name);
        dto.setAdParameterIds(List.of(ID_ONE));
        dto.setUserParameterIds(List.of(ID_ONE));
    }

    @SneakyThrows
    @Test
    void getCategories() {
        var dto = new CategoryDto();
        dto.setId(ID_ONE);
        dto.setName("Тест");
        dto.setImage("");
        mockMvc.perform(get("/public/categories").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(dto))));
    }

    @SneakyThrows
    @Test
    void getCategory() {
        var dto = new CategoryResponseDto()
                .name("Тест").adParameters(List.of())
                .userParameters(List.of()).image("");
        mockMvc.perform(get("/public/category/" + ID_ONE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @SneakyThrows
    @WithMockUser(roles = {"ADMIN"})
    @Test
    void putCategory() {
        var oldEntity = categoryRepository.findByIdWithParameters(ID_ONE).orElseThrow();
        entityManager.clear();
        mockMvc.perform(put("/admin/category/" + ID_ONE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());

        var newEntity = categoryRepository.findByIdWithParameters(ID_ONE).orElseThrow();

        assertAll(
                () -> assertNotEquals(oldEntity.getName(), newEntity.getName(),"Name not change"),
                () -> assertTrue(oldEntity.getAdParameters().isEmpty(),
                        "List of ad parameter of old entity not empty"),
                () -> assertTrue(oldEntity.getUserParameters().isEmpty(),
                        "List of user parameter of old entity not empty"),
                () -> assertEquals(name, newEntity.getName(), "Name not change"),
                () -> assertEquals(1, newEntity.getAdParameters().size()),
                () -> assertEquals(AD_NAME,
                        newEntity.getAdParameters().stream().findFirst().orElseThrow().getName(),
                        "Not found ad parameter"),
                () -> assertEquals(1, newEntity.getUserParameters().size()),
                () -> assertEquals(USER_NAME,
                        newEntity.getUserParameters().stream().findFirst().orElseThrow().getName(),
                        "Not found user parameter")
        );
    }

    @SneakyThrows
    @Test
    void putCategoryByAnonymousUser() {
        mockMvc.perform(put("/admin/category/" + ID_ONE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @SneakyThrows
    @WithMockUser(roles = {"ADMIN"})
    @Test
    void addCategory() {
        mockMvc.perform(post("/admin/category")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().json(String.valueOf(ID_TWO)));

        var entity = categoryRepository.findByIdWithParameters(ID_TWO).orElseThrow();

        assertAll(
                () -> assertEquals(name, entity.getName()),
                () -> assertEquals(1, entity.getAdParameters().size()),
                () -> assertEquals(AD_NAME,
                        entity.getAdParameters().stream().findFirst().orElseThrow().getName(),
                        "Not found ad parameter"),
                () -> assertEquals(1, entity.getUserParameters().size()),
                () -> assertEquals(USER_NAME,
                        entity.getUserParameters().stream().findFirst().orElseThrow().getName(),
                        "Not found user parameter")
        );
    }

    @SneakyThrows
    @Test
    void addCategoryByAnonymousUser() {
        mockMvc.perform(post("/admin/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @SneakyThrows
    @WithMockUser(roles = {"ADMIN"})
    @Test
    void deleteCategory() {
        mockMvc.perform(delete("/admin/category/" + ID_ONE))
                .andExpect(status().isNoContent());
        assertThrows(NoSuchElementException.class, () ->
                categoryRepository.findById(ID_ONE).orElseThrow());
    }

    @SneakyThrows
    @Test
    void deleteCategoryByAnonymousUser() {
        mockMvc.perform(delete("/admin/category/" + ID_ONE))
                .andExpect(status().isUnauthorized());
    }
}
