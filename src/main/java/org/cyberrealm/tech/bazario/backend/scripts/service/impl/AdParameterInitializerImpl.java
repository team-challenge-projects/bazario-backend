package org.cyberrealm.tech.bazario.backend.scripts.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.script.ParameterCredentials;
import org.cyberrealm.tech.bazario.backend.model.Ad;
import org.cyberrealm.tech.bazario.backend.model.AdParameter;
import org.cyberrealm.tech.bazario.backend.model.TypeAdParameter;
import org.cyberrealm.tech.bazario.backend.repository.AdParameterRepository;
import org.cyberrealm.tech.bazario.backend.scripts.service.AdParameterInitializer;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdParameterInitializerImpl implements AdParameterInitializer {
    private final AdParameterRepository repository;

    @Override
    public void addParameters(List<ParameterCredentials> credentials, List<Ad> ads,
                              List<TypeAdParameter> adTypes) {
        Specification<AdParameter> spec = (root, query,
                                           cb) ->
                credentials.stream().map(dto -> {
                    var ownerIdPredicate = cb.equal(root.get("ad").get("id"),
                            ads.get(dto.getOwnerId()).getId());
                    var typeIdPredicate = cb.equal(root.get("parameter").get("id"),
                            adTypes.get(dto.getTypeParameter()).getId());
                    var parameterValuePredicate = cb.equal(root.get("parameterValue"),
                            dto.getParameterValue());
                    return cb.and(ownerIdPredicate, typeIdPredicate, parameterValuePredicate);
                }).reduce(cb::or).orElse(cb.disjunction());

        var existsParameters = repository.findAll(spec);

        var notExistsCredentials = credentials.stream().filter(cred ->
                existsParameters.stream().noneMatch(param ->
                        param.getAd().getId().equals(ads.get(cred.getOwnerId()).getId())
                                && param.getParameter().getId().equals(adTypes.get(
                                        cred.getTypeParameter()).getId())
                                && param.getParameterValue().equals(cred.getParameterValue())))
                .toList();

        if (!notExistsCredentials.isEmpty()) {
            notExistsCredentials.forEach(cred -> {
                var param = new AdParameter();
                param.setAd(ads.get(cred.getOwnerId()));
                param.setParameter(adTypes.get(cred.getTypeParameter()));
                param.setParameterValue(cred.getParameterValue());
                repository.save(param);
            });
        }
    }
}
