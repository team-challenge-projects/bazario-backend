package org.cyberrealm.tech.bazario.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.cyberrealm.tech.bazario.backend.dto.BasicAdminParameter;
import org.cyberrealm.tech.bazario.backend.dto.BasicAdminParameterResponse;
import org.cyberrealm.tech.bazario.backend.mapper.TypeUserParameterMapper;
import org.cyberrealm.tech.bazario.backend.model.TypeUserParameter;
import org.cyberrealm.tech.bazario.backend.repository.TypeUserParameterRepository;
import org.cyberrealm.tech.bazario.backend.service.impl.TypeUserParameterServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TypeUserParameterServiceTest {
    public static final long ONE_ID = 1L;
    @Mock
    private TypeUserParameterRepository repository;
    @Mock
    private TypeUserParameterMapper mapper;
    @InjectMocks
    private TypeUserParameterServiceImpl service;

    @Test
    void create() {
        var dto = new BasicAdminParameter();
        var entity = new TypeUserParameter();
        entity.setId(ONE_ID);
        when(mapper.toTypeUserParameter(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);

        assertEquals(ONE_ID, service.create(dto));
    }

    @Test
    void delete() {
        service.delete(ONE_ID);
        verify(repository).deleteById(ONE_ID);
    }

    @Test
    void update() {
        var dto = new BasicAdminParameter();
        var entity = new TypeUserParameter();
        entity.setId(ONE_ID);
        when(mapper.toTypeUserParameter(ONE_ID, dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);

        var response = new BasicAdminParameterResponse();
        when(mapper.toBasicAdminParameter(entity)).thenReturn(response);

        assertEquals(response, service.update(ONE_ID, dto));
    }
}
