package org.cyberrealm.tech.bazario.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.model.AdParameter;
import org.cyberrealm.tech.bazario.backend.projection.IdProjection;
import org.cyberrealm.tech.bazario.backend.repository.AdParameterRepository;
import org.cyberrealm.tech.bazario.backend.service.AdParameterService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdParameterServiceImpl implements AdParameterService {
    private AdParameterRepository parameterRepository;

    @Override
    public List<Long> filterByParam(Map<Long, String> filters) {
        Specification<AdParameter> spec = (root, query, builder) -> {
            query.distinct(true);
            query.select(root.get("ad").get("id"));
            return filters.entrySet().stream().map(entry -> {
                var idParam = builder.equal(root.get("parameter").get("id"), entry.getKey());
                var value = builder.equal(root.get("value"), entry.getValue());
                return builder.and(idParam, value);
            }).reduce(builder::or).orElse(builder.conjunction());
        };
        return parameterRepository.findAll(spec, IdProjection.class).stream().map(IdProjection::getId).toList();
    }
}
