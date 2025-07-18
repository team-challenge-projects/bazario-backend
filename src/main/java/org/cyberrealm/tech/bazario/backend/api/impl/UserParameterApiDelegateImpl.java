package org.cyberrealm.tech.bazario.backend.api.impl;

import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.api.TypeUserParameterApiDelegate;
import org.cyberrealm.tech.bazario.backend.dto.BasicAdminParameter;
import org.cyberrealm.tech.bazario.backend.dto.BasicAdminParameterResponse;
import org.cyberrealm.tech.bazario.backend.service.TypeUserParameterService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserParameterApiDelegateImpl implements TypeUserParameterApiDelegate {
    private final TypeUserParameterService service;

    @Override
    public ResponseEntity<Long> createUserParameter(BasicAdminParameter basicAdminParameter) {
        return ResponseEntity.ok(service.create(basicAdminParameter));
    }

    @Override
    public ResponseEntity<Void> deleteUserParameter(Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<BasicAdminParameterResponse> putUserParameter(
            Long id, BasicAdminParameter basicAdminParameter) {
        return ResponseEntity.ok(service.update(id, basicAdminParameter));
    }
}
