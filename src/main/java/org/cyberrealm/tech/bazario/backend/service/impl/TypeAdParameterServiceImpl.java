package org.cyberrealm.tech.bazario.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.BasicAdminParameter;
import org.cyberrealm.tech.bazario.backend.mapper.TypeAdParameterMapper;
import org.cyberrealm.tech.bazario.backend.repository.TypeAdParameterRepository;
import org.cyberrealm.tech.bazario.backend.service.TypeAdParameterService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TypeAdParameterServiceImpl implements TypeAdParameterService {
    private final TypeAdParameterRepository parameterRepository;
    private final TypeAdParameterMapper mapper;

    @Override
    public void create(BasicAdminParameter parameter) {
        parameterRepository.save(mapper.toTypeAdParameter(parameter));
    }

    @Override
    public void update(Long id, BasicAdminParameter parameter) {
        parameterRepository.save(mapper.toTypeAdParameter(id, parameter));
    }

    @Override
    public void delete(Long id) {
        parameterRepository.deleteById(id);
    }
}
