package org.cyberrealm.tech.bazario.backend.scripts.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
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

    @Override
    public List<TypeAdParameter> getAdTypes(List<BasicTypeParameter> parameters) {
        var existTypeParam = repository.findByNameIn(parameters.stream()
                .map(BasicTypeParameter::getName).toList());
        List<BasicTypeParameter> notExistParameter = getNotExistParameter(
                existTypeParam, parameters);

        if (notExistParameter.isEmpty()) {
            return existTypeParam;
        }

        var notExistTypeParam = createParameters(notExistParameter);
        return Stream.of(existTypeParam, notExistTypeParam).flatMap(Collection::stream).toList();
    }

    private List<TypeAdParameter> createParameters(List<BasicTypeParameter> parameters) {
        var typesParam = parameters.stream().map(mapper::toTypeAdParameter).toList();
        return repository.saveAll(typesParam);
    }

    private List<BasicTypeParameter> getNotExistParameter(List<TypeAdParameter> typeParam,
                                                          List<BasicTypeParameter> parameters) {
        var namesParam = typeParam.stream().map(TypeAdParameter::getName).toList();
        return parameters.stream().filter(pram ->
                !namesParam.contains(pram.getName())).toList();
    }

    private TypeAdParameter getNewParameter(BasicTypeParameter parameter) {
        return repository.save(mapper.toTypeAdParameter(parameter));
    }
}
