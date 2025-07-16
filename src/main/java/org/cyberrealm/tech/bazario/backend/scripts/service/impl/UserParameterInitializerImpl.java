package org.cyberrealm.tech.bazario.backend.scripts.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.script.ParameterCredentials;
import org.cyberrealm.tech.bazario.backend.model.TypeUserParameter;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.model.UserParameter;
import org.cyberrealm.tech.bazario.backend.repository.UserParameterRepository;
import org.cyberrealm.tech.bazario.backend.scripts.service.UserParameterInitializer;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserParameterInitializerImpl implements UserParameterInitializer {
    private final UserParameterRepository repository;

    @Override
    public void addParameters(List<ParameterCredentials> parameters, List<User> users,
                              List<TypeUserParameter> types) {
        Specification<UserParameter> spec = (root, query, cb) ->
                parameters.stream().map(dto -> {
                    var ownerIdPredicate = cb.equal(root.get("user").get("id"),
                            users.get(dto.getOwnerId()).getId());
                    var typeIdPredicate = cb.equal(root.get("parameter").get("id"),
                            types.get(dto.getTypeParameter()).getId());
                    var parameterValuePredicate = cb.equal(root.get("parameterValue"),
                            dto.getParameterValue());
                    return cb.and(ownerIdPredicate, typeIdPredicate, parameterValuePredicate);
                }).reduce(cb::or).orElse(cb.disjunction());

        var existsParameters = repository.findAll(spec);

        var notExistsCredentials = parameters.stream().filter(credentials ->
                existsParameters.stream().noneMatch(param ->
                        param.getUser().getId().equals(users.get(credentials.getOwnerId()).getId())
                                && param.getParameter().getId().equals(types.get(
                                        credentials.getTypeParameter()).getId())
                                && param.getParameterValue().equals(
                                        credentials.getParameterValue())))
                .toList();

        if (!notExistsCredentials.isEmpty()) {
            notExistsCredentials.forEach(credentials -> {
                var param = new UserParameter();
                param.setUser(users.get(credentials.getOwnerId()));
                param.setParameter(types.get(credentials.getTypeParameter()));
                param.setParameterValue(credentials.getParameterValue());
                repository.save(param);
            });
        }

    }
}
