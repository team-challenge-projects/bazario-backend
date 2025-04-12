package org.cyberrealm.tech.bazario.backend.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.model.UserParameter;
import org.cyberrealm.tech.bazario.backend.projection.UserIdProjection;
import org.cyberrealm.tech.bazario.backend.repository.UserParameterRepository;
import org.cyberrealm.tech.bazario.backend.service.UserParameterService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserParameterServiceImpl implements UserParameterService {
    private UserParameterRepository parameterRepository;

    @Override
    public List<Long> filterByParam(Map<Long, String> filters) {
        Specification<UserParameter> spec = (root, query, builder) -> {
            Objects.requireNonNull(query).distinct(true);
            query.select(root.get("user").get("id"));
            return filters.entrySet().stream().map(entry -> {
                var idParam = builder.equal(root.get("parameter").get("id"), entry.getKey());
                var value = entry.getValue().contains("|")
                        ? root.get("value").in((Object[]) entry.getValue().split("\\|"))
                        : builder.equal(root.get("value"), entry.getValue());
                return builder.and(idParam, value);
            }).reduce(builder::and).orElse(builder.conjunction());
        };
        return parameterRepository.findAll(spec, UserIdProjection.class).stream()
                .map(UserIdProjection::getUserId).toList();
    }
}
