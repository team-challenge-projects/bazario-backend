package org.cyberrealm.tech.bazario.backend.scripts.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.script.AdCredentials;
import org.cyberrealm.tech.bazario.backend.mapper.AdMapper;
import org.cyberrealm.tech.bazario.backend.model.AdParameter;
import org.cyberrealm.tech.bazario.backend.model.Category;
import org.cyberrealm.tech.bazario.backend.model.TypeAdParameter;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.repository.AdRepository;
import org.cyberrealm.tech.bazario.backend.scripts.service.AdInitializer;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdInitializerImpl implements AdInitializer {
    private final AdRepository repository;
    private final AdMapper mapper;

    @Override
    public void createAd(AdCredentials credentials, List<User> users, List<Category> categories,
                         List<TypeAdParameter> adTypes) {
        if (!repository.existsByTitleAndPrice(credentials.getTitle(), credentials.getPrice())) {
            var ad = mapper.toAd(credentials);
            ad.setUser(users.get(credentials.getUser()));
            ad.setCategory(categories.get(credentials.getCategory()));
            ad.setParameters(credentials.getAdParameters().stream().map(parameter -> {
                var adParameter = new AdParameter();
                adParameter.setParameter(adTypes.get(parameter.getTypeParameter()));
                adParameter.setParameterValue(parameter.getParameterValue());
                adParameter.setAd(ad);
                return adParameter;
            }).collect(Collectors.toSet()));
            repository.save(ad);
        }

    }
}
