package org.cyberrealm.tech.bazario.backend.service.impl;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.BasicAdminParameter;
import org.cyberrealm.tech.bazario.backend.dto.BasicAdminParameterResponse;
import org.cyberrealm.tech.bazario.backend.mapper.TypeAdParameterMapper;
import org.cyberrealm.tech.bazario.backend.repository.TypeAdParameterRepository;
import org.cyberrealm.tech.bazario.backend.service.PageableService;
import org.cyberrealm.tech.bazario.backend.service.TypeAdParameterService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TypeAdParameterServiceImpl implements TypeAdParameterService {
    private final TypeAdParameterRepository parameterRepository;
    private final TypeAdParameterMapper mapper;
    private final PageableService pageableService;

    @Override
    public Page<BasicAdminParameterResponse> getAll(Map<String, String> filters) {
        return parameterRepository.findAll(pageableService.get(filters))
                .map(mapper::toBasicAdminParameter);
    }

    @Override
    public Long create(BasicAdminParameter parameter) {
        return parameterRepository.save(mapper.toTypeAdParameter(parameter))
                .getId();
    }

    @Override
    public BasicAdminParameterResponse update(Long id, BasicAdminParameter parameter) {
        return mapper.toBasicAdminParameter(parameterRepository
                .save(mapper.toTypeAdParameter(id, parameter)));
    }

    @Override
    public void delete(Long id) {
        parameterRepository.deleteById(id);
    }
}
