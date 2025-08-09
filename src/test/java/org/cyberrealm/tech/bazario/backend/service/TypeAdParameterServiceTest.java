package org.cyberrealm.tech.bazario.backend.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import org.cyberrealm.tech.bazario.backend.dto.BasicAdminParameter;
import org.cyberrealm.tech.bazario.backend.dto.BasicAdminParameterResponse;
import org.cyberrealm.tech.bazario.backend.dto.BasicUserParameter;
import org.cyberrealm.tech.bazario.backend.exception.custom.ArgumentNotValidException;
import org.cyberrealm.tech.bazario.backend.mapper.TypeAdParameterMapper;
import org.cyberrealm.tech.bazario.backend.model.TypeAdParameter;
import org.cyberrealm.tech.bazario.backend.repository.TypeAdParameterRepository;
import org.cyberrealm.tech.bazario.backend.service.impl.TypeAdParameterServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class TypeAdParameterServiceTest {
    private static final long ONE_ID = 1L;
    private static final String FAIL = "fail";
    private static final String DESCRIPTION = "value is Test";
    private static final long TWO_ID = 2L;
    @Mock
    private TypeAdParameterRepository parameterRepository;
    @Mock
    private TypeAdParameterMapper mapper;
    @Mock
    private PageableService pageableService;
    @InjectMocks
    private TypeAdParameterServiceImpl service;

    @Test
    void getAll() {
        Map<String, String> filter = Map.of();
        var pageable = PageRequest.of(0, 16);
        when(pageableService.get(filter)).thenReturn(pageable);

        var typyParameter = new TypeAdParameter();
        var page = new PageImpl<>(List.of(typyParameter), pageable, 1L);
        when(parameterRepository.findAll(pageable)).thenReturn(page);

        var dto = new BasicAdminParameterResponse();
        when(mapper.toBasicAdminParameter(typyParameter))
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
        when(mapper.toTypeAdParameter(dto)).thenReturn(entity);
        when(parameterRepository.save(entity)).thenReturn(entity);

        assertEquals(ONE_ID, service.create(dto));
    }

    @Test
    void update() {
        var entity = new TypeAdParameter();
        entity.setId(ONE_ID);
        var dto = new BasicAdminParameter();
        when(mapper.toTypeAdParameter(ONE_ID, dto)).thenReturn(entity);
        when(parameterRepository.save(entity)).thenReturn(entity);

        var response = new BasicAdminParameterResponse();
        when(mapper.toBasicAdminParameter(entity)).thenReturn(response);

        assertEquals(response, service.update(ONE_ID, dto));
    }

    @Test
    void delete() {
        service.delete(ONE_ID);
        verify(parameterRepository).deleteById(ONE_ID);
    }

    @Test
    void checkParametersFail() {
        var entity = new TypeAdParameter();
        entity.setId(ONE_ID);
        entity.setRestrictionPattern("^Test$");
        entity.setDescriptionPattern(DESCRIPTION);
        var entityTwo = new TypeAdParameter();
        entityTwo.setId(TWO_ID);
        entityTwo.setRestrictionPattern("^TestTwo$");
        entityTwo.setDescriptionPattern(DESCRIPTION + "Two");
        when(parameterRepository.findAllById(List.of(ONE_ID, TWO_ID)))
                .thenReturn(List.of(entity, entityTwo));

        var adParamOne = new BasicUserParameter().typeId(ONE_ID)
                .parameterValue(FAIL);
        var adParamTwo = new BasicUserParameter().typeId(TWO_ID)
                .parameterValue(FAIL + "Two");
        var exception = assertThrows(ArgumentNotValidException.class,
                () -> service.checkParameters(List.of(adParamOne, adParamTwo)));
        assertEquals("%s is not matches, than %s;<br>%s is not matches, than %s."
                        .formatted(FAIL, DESCRIPTION, FAIL + "Two", DESCRIPTION + "Two"),
                exception.getMessage());
    }
}
