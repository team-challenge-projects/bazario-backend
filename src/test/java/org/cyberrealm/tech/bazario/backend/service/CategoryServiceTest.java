package org.cyberrealm.tech.bazario.backend.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.NoSuchElementException;
import org.cyberrealm.tech.bazario.backend.dto.CategoryRequestDto;
import org.cyberrealm.tech.bazario.backend.mapper.impl.CategoryMapperImpl;
import org.cyberrealm.tech.bazario.backend.model.TypeAdParameter;
import org.cyberrealm.tech.bazario.backend.model.TypeUserParameter;
import org.cyberrealm.tech.bazario.backend.repository.CategoryRepository;
import org.cyberrealm.tech.bazario.backend.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@Import({
        CategoryServiceImpl.class,
        CategoryMapperImpl.class
})
@ExtendWith(SpringExtension.class)
class CategoryServiceTest {
    private static final String AD_NAME = "Доставка тест";
    private static final String USER_NAME = "Тестовий тип";
    private static final long ID = 1L;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TestEntityManager entityManager;
    private final CategoryRequestDto dto = new CategoryRequestDto();
    private final String name = "Test";

    @BeforeEach
    void beforeEach() {
        dto.setName(name);
        dto.setAdParameterIds(List.of(ID));
        dto.setUserParameterIds(List.of(ID));

        var adParam = new TypeAdParameter();
        adParam.setName(AD_NAME);
        adParam.setRestrictionPattern("^ТестПошта$");
        adParam.setDescriptionPattern("Це тестова пошта");
        entityManager.persist(adParam);

        var userParam = new TypeUserParameter();
        userParam.setName(USER_NAME);
        userParam.setRestrictionPattern("^ТестТип$");
        userParam.setDescriptionPattern("Це тестовий тип юзера");
        entityManager.persist(userParam);

        entityManager.flush();
    }

    @Test
    void add() {
        categoryService.add(dto);

        var entity = categoryRepository.findByIdWithParameters(ID).orElseThrow();
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

    @Test
    void put() {
        categoryService.add(dto);

        String newName = "ChangeTest";
        dto.setName(newName);
        dto.setAdParameterIds(List.of());
        dto.setUserParameterIds(List.of());

        var oldEntity = categoryRepository.findByIdWithParameters(ID)
                .orElseThrow();

        entityManager.clear();

        categoryService.put(ID, dto);

        var newEntity = categoryRepository.findByIdWithParameters(ID).orElseThrow();

        assertAll(
                () -> assertNotEquals(oldEntity.getName(), newEntity.getName()),
                () -> assertTrue(newEntity.getAdParameters().isEmpty()),
                () -> assertTrue(newEntity.getUserParameters().isEmpty()),
                () -> assertEquals(newName, newEntity.getName())
        );
    }

    @Test
    void delete() {
        categoryService.add(dto);
        categoryService.delete(ID);
        assertThrows(NoSuchElementException.class, () ->
                categoryRepository.findById(ID).orElseThrow());
    }

    @Test
    void getCategoryWithParameters() {
    }

    @Test
    void get() {
    }

    @Test
    void save() {
    }
}
