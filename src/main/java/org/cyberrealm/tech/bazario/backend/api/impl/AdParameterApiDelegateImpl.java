package org.cyberrealm.tech.bazario.backend.api.impl;

import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.api.AdParameterApiDelegate;
import org.cyberrealm.tech.bazario.backend.dto.BasicAdminParameter;
import org.cyberrealm.tech.bazario.backend.service.TypeAdParameterService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdParameterApiDelegateImpl implements AdParameterApiDelegate {
    private final TypeAdParameterService parameterService;

    @Override
    public ResponseEntity<Void> createAdParameter(BasicAdminParameter basicAdminParameter) {
        parameterService.create(basicAdminParameter);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> deleteAdParameter(Long id) {
        parameterService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> putAdParameter(Long id, BasicAdminParameter basicAdminParameter) {
        parameterService.update(id, basicAdminParameter);
        return ResponseEntity.noContent().build();
    }
}
