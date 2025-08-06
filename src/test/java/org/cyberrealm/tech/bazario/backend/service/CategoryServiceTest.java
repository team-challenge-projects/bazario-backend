package org.cyberrealm.tech.bazario.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.cyberrealm.tech.bazario.backend.dto.CategoryRequestDto;
import org.cyberrealm.tech.bazario.backend.mapper.CategoryMapper;
import org.cyberrealm.tech.bazario.backend.model.Category;
import org.cyberrealm.tech.bazario.backend.repository.CategoryRepository;
import org.cyberrealm.tech.bazario.backend.repository.TypeAdParameterRepository;
import org.cyberrealm.tech.bazario.backend.repository.TypeUserParameterRepository;
import org.cyberrealm.tech.bazario.backend.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    private static final long ONE_ID = 1L;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private TypeAdParameterRepository adParamRepository;
    @Mock
    private TypeUserParameterRepository userParamRepository;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(ONE_ID);
        category.setName("OLd value");
        category.setAdParameters(Set.of());
        category.setUserParameters(Set.of());
    }

    @Test
    void add() {
        when(adParamRepository.findAllById(List.of())).thenReturn(List.of());
        when(userParamRepository.findAllById(List.of())).thenReturn(List.of());

        var dto = new CategoryRequestDto()
                .adParameterIds(List.of())
                .userParameterIds(List.of())
                .name("Test");

        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        assertEquals(ONE_ID, categoryService.add(dto));
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void put() {
        when(adParamRepository.findAllById(List.of())).thenReturn(List.of());
        when(userParamRepository.findAllById(List.of())).thenReturn(List.of());

        when(categoryRepository.findById(ONE_ID)).thenReturn(Optional.of(category));
        var dto = new CategoryRequestDto()
                .adParameterIds(List.of())
                .userParameterIds(List.of())
                .name("Test");

        categoryService.put(ONE_ID, dto);

        assertEquals("Test", category.getName());
        verify(categoryRepository).save(category);
    }

    @Test
    void delete() {
        when(categoryRepository.findById(ONE_ID)).thenReturn(Optional.of(category));

        categoryService.delete(ONE_ID);

        verify(categoryRepository).delete(category);
    }

    @Test
    void getCategoryWithParameters() {
        when(categoryRepository.findByIdWithParameters(ONE_ID))
                .thenReturn(Optional.of(category));

        categoryService.getCategoryWithParameters(ONE_ID);

        verify(categoryMapper).toCategoryDto(category);
    }

    @Test
    void getAll() {
        when(categoryRepository.findAll()).thenReturn(List.of(category));
        var dto = categoryService.getAll();

        assertEquals(category.getId(), dto.get(0).getId());
        assertEquals(category.getName(), dto.get(0).getName());
    }

    @Test
    void get() {
        when(categoryRepository.findById(ONE_ID)).thenReturn(Optional.of(category));
        var entity = categoryService.get(ONE_ID);

        assertEquals(category.getId(), entity.getId());
        assertEquals(category.getName(), entity.getName());
    }

    @Test
    void save() {
        categoryService.save(category);
        verify(categoryRepository).save(category);
    }
}
