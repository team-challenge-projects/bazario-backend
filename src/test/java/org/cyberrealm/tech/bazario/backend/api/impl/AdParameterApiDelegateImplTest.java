package org.cyberrealm.tech.bazario.backend.api.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import java.util.NoSuchElementException;
import lombok.SneakyThrows;
import org.cyberrealm.tech.bazario.backend.AbstractIntegrationTest;
import org.cyberrealm.tech.bazario.backend.dto.BasicAdminParameter;
import org.cyberrealm.tech.bazario.backend.repository.TypeAdParameterRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

class AdParameterApiDelegateImplTest extends AbstractIntegrationTest {
    private static BasicAdminParameter dto;

    @Autowired
    private TypeAdParameterRepository repository;

    @BeforeAll
    static void beforeAll() {
        dto = new BasicAdminParameter()
                .name("Test")
                .restrictionPattern("^Test$")
                .descriptionPattern("The test");
    }

    @SneakyThrows
    @Test
    void getAdParameters() {
        var result = mockMvc.perform(get("/public/ad/parameters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of())))
                .andReturn().getResponse().getContentAsByteArray();
        var rootNode = objectMapper.readTree(result);
        assertAll(
                () -> assertEquals(1, rootNode.get("totalElements").asInt()),
                () -> assertEquals(16, rootNode.get("pageable").get("pageSize").asInt()),
                () -> assertEquals(1, rootNode.get("content").size()),
                () -> assertEquals("Доставка тест",
                        rootNode.get("content").get(0).get("name").asText())
        );

    }

    @SneakyThrows
    @WithMockUser(roles = {"ADMIN"})
    @Test
    void createAdParameter() {
        mockMvc.perform(post("/admin/ad/parameter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().json(String.valueOf(ID_TWO)));

        var entity = repository.findById(ID_TWO).orElseThrow();
        assertAll(
                () -> assertEquals(dto.getName(), entity.getName()),
                () -> assertEquals(dto.getRestrictionPattern(),
                        entity.getRestrictionPattern()),
                () -> assertEquals(dto.getDescriptionPattern(),
                        entity.getDescriptionPattern())
        );
    }

    @SneakyThrows
    @WithMockUser(roles = {"ADMIN"})
    @Test
    void deleteAdParameter() {
        mockMvc.perform(delete("/admin/ad/parameter/" + ID_ONE))
                .andExpect(status().isNoContent());
        assertThrows(NoSuchElementException.class,
                () -> repository.findById(ID_ONE).orElseThrow());
    }

    @SneakyThrows
    @WithMockUser(roles = {"ADMIN"})
    @Test
    void putAdParameter() {
        var oldEntity = repository.findById(ID_ONE).orElseThrow();
        entityManager.clear();
        mockMvc.perform(put("/admin/ad/parameter/" + ID_ONE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
        var newEntity = repository.findById(ID_ONE).orElseThrow();
        assertAll(
                () -> assertEquals(oldEntity.getId(), newEntity.getId()),
                () -> assertNotEquals(oldEntity.getName(), newEntity.getName()),
                () -> assertNotEquals(oldEntity.getRestrictionPattern(),
                        newEntity.getRestrictionPattern()),
                () -> assertNotEquals(oldEntity.getDescriptionPattern(),
                        newEntity.getDescriptionPattern()),
                () -> assertEquals(dto.getName(), newEntity.getName()),
                () -> assertEquals(dto.getRestrictionPattern(),
                        newEntity.getRestrictionPattern()),
                () -> assertEquals(dto.getDescriptionPattern(),
                        newEntity.getDescriptionPattern())
        );
    }
}
