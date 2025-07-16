package org.cyberrealm.tech.bazario.backend.scripts.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.script.BasicTypeParameter;
import org.cyberrealm.tech.bazario.backend.mapper.TypeUserParameterMapper;
import org.cyberrealm.tech.bazario.backend.model.TypeUserParameter;
import org.cyberrealm.tech.bazario.backend.repository.TypeUserParameterRepository;
import org.cyberrealm.tech.bazario.backend.scripts.service.UserTypeInitializer;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserTypeInitializerImpl implements UserTypeInitializer {
    private final TypeUserParameterRepository repository;
    private final TypeUserParameterMapper mapper;

    @Override
    public List<TypeUserParameter> getUserType(List<BasicTypeParameter> parameters) {
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

    private List<TypeUserParameter> createParameters(List<BasicTypeParameter> parameters) {
        var typesParam = parameters.stream().map(mapper::toTypeUserParameter).toList();
        return repository.saveAll(typesParam);
    }

    private List<BasicTypeParameter> getNotExistParameter(
            List<TypeUserParameter> typeParam, List<BasicTypeParameter> parameters) {
        var namesParam = typeParam.stream().map(TypeUserParameter::getName).toList();
        return parameters.stream().filter(pram ->
                !namesParam.contains(pram.getName())).toList();
    }
}
