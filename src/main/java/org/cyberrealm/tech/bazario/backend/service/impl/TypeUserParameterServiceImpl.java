package org.cyberrealm.tech.bazario.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.BasicAdminParameter;
import org.cyberrealm.tech.bazario.backend.mapper.TypeUserParameterMapper;
import org.cyberrealm.tech.bazario.backend.repository.TypeUserParameterRepository;
import org.cyberrealm.tech.bazario.backend.service.TypeUserParameterService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TypeUserParameterServiceImpl implements TypeUserParameterService {
    private final TypeUserParameterRepository repository;
    private final TypeUserParameterMapper mapper;

    @Override
    public Long create(BasicAdminParameter parameter) {
        return repository.save(mapper.toTypeUserParameter(parameter)).getId();
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void update(Long id, BasicAdminParameter parameter) {
        repository.save(mapper.toTypeUserParameter(id, parameter));
    }
}
