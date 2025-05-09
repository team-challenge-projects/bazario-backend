package org.cyberrealm.tech.bazario.backend.scripts.service.impl;

import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.script.BasicTypeParameter;
import org.cyberrealm.tech.bazario.backend.mapper.TypeAdParameterMapper;
import org.cyberrealm.tech.bazario.backend.model.TypeAdParameter;
import org.cyberrealm.tech.bazario.backend.repository.TypeAdParameterRepository;
import org.cyberrealm.tech.bazario.backend.scripts.service.AdTypeInitializer;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdTypeInitializerImpl implements AdTypeInitializer {
    private final TypeAdParameterRepository repository;
    private final TypeAdParameterMapper mapper;

    @Override
    public TypeAdParameter getAdType(BasicTypeParameter parameter) {
        return repository.findByName(parameter.getName())
                .orElseGet(() -> getNewParameter(parameter));
    }

    private TypeAdParameter getNewParameter(BasicTypeParameter parameter) {
        return repository.save(mapper.toTypeAdParameter(parameter));
    }
}
