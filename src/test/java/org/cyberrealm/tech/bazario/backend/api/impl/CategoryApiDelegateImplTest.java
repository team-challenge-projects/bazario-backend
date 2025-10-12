package org.cyberrealm.tech.bazario.backend.api.impl;

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
import org.cyberrealm.tech.bazario.backend.dto.BasicAdminParameterCategory;
import org.cyberrealm.tech.bazario.backend.dto.BasicItem;
import org.cyberrealm.tech.bazario.backend.dto.CategoryDto;
import org.cyberrealm.tech.bazario.backend.dto.CategoryRequestDto;
import org.cyberrealm.tech.bazario.backend.dto.CategoryResponseDto;
import org.cyberrealm.tech.bazario.backend.dto.TypeView;
import org.cyberrealm.tech.bazario.backend.repository.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

class CategoryApiDelegateImplTest extends AbstractIntegrationTest {
    private static final String AD_NAME = "ТестПошта";
    private static final String USER_NAME = "Тестовий тип";
    private static final String name = "New Test";
    private static final CategoryRequestDto dto = new CategoryRequestDto();
    private static final long ID_THREE = 3L;

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
        dto.setImage("http://test/test.png");
        var dtoTwo = new CategoryDto();
        dtoTwo.setId(ID_TWO);
        dtoTwo.setName("Тест_two");
        dtoTwo.setImage("http://test/test2.png");
        mockMvc.perform(get("/public/categories").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(dto, dtoTwo))));
    }

    @SneakyThrows
    @Test
    void getCategory() {
        var itemOne = new BasicItem().id(ID_ONE).name("ТестПошта");
        var itemTwo = new BasicItem().id(ID_TWO).name("ТестовийСклад");
        var adParam = new BasicAdminParameterCategory()
                .id(ID_ONE).name("Доставка тест")
                .typeView(TypeView.CHECKBOX)
                .descriptionPattern("Це тестова пошта")
                .values(List.of(itemOne, itemTwo));
        var dto = new CategoryResponseDto()
                .name("Тест").adParameters(List.of(adParam))
                .userParameters(List.of()).image("http://test/test.png");
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

        Assertions.assertAll(
                () -> Assertions.assertNotEquals(oldEntity.getName(), newEntity.getName(),
                        "Name not change"),
                () -> Assertions.assertFalse(oldEntity.getAdParameters().isEmpty(),
                        "List of ad parameter is empty"),
                () -> Assertions.assertTrue(oldEntity.getUserParameters().isEmpty(),
                        "List of user parameter of old entity not empty"),
                () -> Assertions.assertEquals(name, newEntity.getName(), "Name not change"),
                () -> Assertions.assertEquals(1, newEntity.getAdParameters().size()),
                () -> Assertions.assertEquals(AD_NAME,
                        newEntity.getAdParameters().stream().findFirst().orElseThrow().getName(),
                        "Not found ad parameter"),
                () -> Assertions.assertEquals(1, newEntity.getUserParameters().size()),
                () -> Assertions.assertEquals(USER_NAME,
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
                .andExpect(content().json(String.valueOf(ID_THREE)));

        var entity = categoryRepository.findByIdWithParameters(ID_THREE).orElseThrow();

        Assertions.assertAll(
                () -> Assertions.assertEquals(name, entity.getName()),
                () -> Assertions.assertEquals(1, entity.getAdParameters().size()),
                () -> Assertions.assertEquals(AD_NAME,
                        entity.getAdParameters().stream().findFirst().orElseThrow().getName(),
                        "Not found ad parameter"),
                () -> Assertions.assertEquals(1, entity.getUserParameters().size()),
                () -> Assertions.assertEquals(USER_NAME,
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
        Assertions.assertThrows(NoSuchElementException.class, () ->
                categoryRepository.findById(ID_ONE).orElseThrow());
    }

    @SneakyThrows
    @Test
    void deleteCategoryByAnonymousUser() {
        mockMvc.perform(delete("/admin/category/" + ID_ONE))
                .andExpect(status().isUnauthorized());
    }
}
