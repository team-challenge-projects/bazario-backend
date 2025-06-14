package org.cyberrealm.tech.bazario.backend.service.impl;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.model.AdParameter;
import org.cyberrealm.tech.bazario.backend.repository.AdParameterRepository;
import org.cyberrealm.tech.bazario.backend.service.AdParameterService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdParameterServiceImpl implements AdParameterService {
    private final AdParameterRepository parameterRepository;

    @Override
    public List<Long> filterByParam(Map<Long, String> filters) {
        Specification<AdParameter> spec = (root, query, builder) -> {
            try {
                return filters.entrySet().stream().map(entry -> {
                    var idParam = builder.equal(root.get("parameter").get("id"), entry.getKey());
                    var value = entry.getValue().contains("|")
                            ? root.get("parameterValue").in((Object[]) entry.getValue()
                            .split("\\|"))
                            : builder.equal(root.get("parameterValue"), entry.getValue());
                    return builder.and(idParam, value);
                }).reduce(builder::and).orElse(builder.conjunction());
            } catch (Exception e) {
                return builder.conjunction();
            }
        };
        return parameterRepository.findAll(spec).stream()
                .map(pram -> pram.getAd().getId()).toList();
    }
}
