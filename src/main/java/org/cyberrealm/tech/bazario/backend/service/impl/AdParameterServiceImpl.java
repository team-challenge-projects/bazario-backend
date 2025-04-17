package org.cyberrealm.tech.bazario.backend.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.model.AdParameter;
import org.cyberrealm.tech.bazario.backend.projection.AdIdProjection;
import org.cyberrealm.tech.bazario.backend.repository.AdParameterRepository;
import org.cyberrealm.tech.bazario.backend.service.AdParameterService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdParameterServiceImpl implements AdParameterService {
    private AdParameterRepository parameterRepository;

    @Override
    public List<Long> filterByParam(Map<Long, String> filters) {
        Specification<AdParameter> spec = (root, query, builder) -> {
            Objects.requireNonNull(query).distinct(true);
            query.select(root.get("ad").get("id"));
            return filters.entrySet().stream().map(entry -> {
                var idParam = builder.equal(root.get("parameter").get("id"), entry.getKey());
                var value = entry.getValue().contains("|")
                        ? root.get("value").in((Object[]) entry.getValue().split("\\|"))
                        : builder.equal(root.get("value"), entry.getValue());
                return builder.and(idParam, value);
            }).reduce(builder::or).orElse(builder.conjunction());
        };
        return parameterRepository.findBy(spec, AdIdProjection.class).stream()
                .map(AdIdProjection::getAdId).toList();
    }
}
