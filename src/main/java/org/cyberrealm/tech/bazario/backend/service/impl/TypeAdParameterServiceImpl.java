package org.cyberrealm.tech.bazario.backend.service.impl;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.BasicAdminParameter;
import org.cyberrealm.tech.bazario.backend.dto.BasicAdminParameterResponse;
import org.cyberrealm.tech.bazario.backend.dto.BasicUserParameter;
import org.cyberrealm.tech.bazario.backend.exception.custom.ArgumentNotValidException;
import org.cyberrealm.tech.bazario.backend.mapper.TypeAdParameterMapper;
import org.cyberrealm.tech.bazario.backend.repository.TypeAdParameterRepository;
import org.cyberrealm.tech.bazario.backend.service.PageableService;
import org.cyberrealm.tech.bazario.backend.service.TypeAdParameterService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TypeAdParameterServiceImpl implements TypeAdParameterService {
    private static final String END_OF_LINE_SEPARATOR = ";";
    private static final String END_DOT = ".";

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
    public void update(Long id, BasicAdminParameter parameter) {
        parameterRepository.save(mapper.toTypeAdParameter(id, parameter));
    }

    @Override
    public void delete(Long id) {
        parameterRepository.deleteById(id);
    }

    @Override
    public void checkParameters(List<BasicUserParameter> adParameters) {
        if (adParameters.isEmpty()) {
            return;
        }
        var types = parameterRepository.findAllById(adParameters.stream()
                .map(BasicUserParameter::getTypeId).toList());
        var builder = new StringBuilder();
        adParameters.forEach(param -> {
            var restrictionPattern = types.stream().filter(type ->
                    type.getId().equals(param.getTypeId())).findFirst();
            if (restrictionPattern.isPresent()) {
                var matchesPattern = restrictionPattern.filter(typeAdParameter ->
                        Pattern.matches(typeAdParameter.getRestrictionPattern(),
                                param.getParameterValue()));
                if (matchesPattern.isEmpty()) {
                    builder.append(param.getParameterValue()).append(" is not matches, than ")
                            .append(restrictionPattern.get().getDescriptionPattern())
                            .append(END_OF_LINE_SEPARATOR);
                }
            } else {
                builder.append("Type parameter with id ").append(param.getTypeId())
                        .append(" is not found");
            }
        });
        if (!builder.isEmpty()) {
            int indexLastSeparator = builder.lastIndexOf(END_OF_LINE_SEPARATOR);
            builder.replace(indexLastSeparator, indexLastSeparator + 1, END_DOT);
            throw new ArgumentNotValidException(builder.toString());
        }
    }
}
