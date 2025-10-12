package org.cyberrealm.tech.bazario.backend.service.impl;

import java.util.Arrays;
import java.util.List;
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
    public List<Long> filterByParam(String ids) {
        Specification<AdParameter> spec = (root, query, builder) ->
                root.get("parameter").get("id").in(Arrays.stream(ids
                        .split("\\|")).map(Long::parseLong).toArray());

        return parameterRepository.findAll(spec).stream()
                .map(pram -> pram.getAd().getId())
                .distinct().toList();
    }
}
