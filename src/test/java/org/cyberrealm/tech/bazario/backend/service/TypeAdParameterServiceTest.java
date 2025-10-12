package org.cyberrealm.tech.bazario.backend.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import org.cyberrealm.tech.bazario.backend.dto.BasicAdminParameter;
import org.cyberrealm.tech.bazario.backend.dto.BasicAdminParameterResponse;
import org.cyberrealm.tech.bazario.backend.mapper.TypeAdParameterMapper;
import org.cyberrealm.tech.bazario.backend.model.TypeAdParameter;
import org.cyberrealm.tech.bazario.backend.repository.AdParameterRepository;
import org.cyberrealm.tech.bazario.backend.repository.TypeAdParameterRepository;
import org.cyberrealm.tech.bazario.backend.service.impl.TypeAdParameterServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class TypeAdParameterServiceTest {
    private static final long ONE_ID = 1L;

    @Mock
    private TypeAdParameterRepository parameterRepository;
    @Mock
    private TypeAdParameterMapper mapper;
    @Mock
    private PageableService pageableService;
    @Mock
    private AdParameterRepository adParameterRepository;
    @InjectMocks
    private TypeAdParameterServiceImpl service;

    @Test
    void getAll() {
        Map<String, String> filter = Map.of();
        var pageable = PageRequest.of(0, 16);
        Mockito.when(pageableService.get(filter)).thenReturn(pageable);

        var typyParameter = new TypeAdParameter();
        var page = new PageImpl<>(List.of(typyParameter), pageable, 1L);
        Mockito.when(parameterRepository.findAll(pageable)).thenReturn(page);

        var dto = new BasicAdminParameterResponse();
        Mockito.when(mapper.toBasicAdminParameter(typyParameter))
                .thenReturn(dto);

        var response = service.getAll(filter);

        assertAll(
                () -> assertEquals(1L, response.getTotalElements()),
                () -> assertEquals(pageable, response.getPageable()),
                () -> assertEquals(dto, response.getContent().get(0))
        );
    }

    @Test
    void create() {
        var dto = new BasicAdminParameter();
        var entity = new TypeAdParameter();
        entity.setId(ONE_ID);
        Mockito.when(mapper.toTypeAdParameter(dto)).thenReturn(entity);
        Mockito.when(parameterRepository.save(entity)).thenReturn(entity);

        assertEquals(ONE_ID, service.create(dto));
    }

    @Test
    void update() {
        var entity = new TypeAdParameter();
        entity.setId(ONE_ID);
        var dto = new BasicAdminParameter();
        Mockito.when(mapper.toTypeAdParameter(ONE_ID, dto)).thenReturn(entity);
        Mockito.when(parameterRepository.save(entity)).thenReturn(entity);

        var response = new BasicAdminParameterResponse();
        Mockito.when(mapper.toBasicAdminParameter(entity)).thenReturn(response);

        assertEquals(response, service.update(ONE_ID, dto));
    }

    @Test
    void delete() {
        service.delete(ONE_ID);
        Mockito.verify(parameterRepository).deleteById(ONE_ID);
    }
}
