package org.cyberrealm.tech.bazario.backend.api.impl;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.api.TypeAdParameterApiDelegate;
import org.cyberrealm.tech.bazario.backend.dto.BasicAdminParameter;
import org.cyberrealm.tech.bazario.backend.dto.BasicAdminParameterResponse;
import org.cyberrealm.tech.bazario.backend.service.TypeAdParameterService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdParameterApiDelegateImpl implements TypeAdParameterApiDelegate {
    private final TypeAdParameterService parameterService;

    @Override
    public ResponseEntity<Page> getAdParameters(Map<String, String> filters) {
        return ResponseEntity.ok(parameterService.getAll(filters));
    }

    @Override
    public ResponseEntity<Long> createAdParameter(BasicAdminParameter basicAdminParameter) {
        return ResponseEntity.ok(parameterService.create(basicAdminParameter));
    }

    @Override
    public ResponseEntity<Void> deleteAdParameter(Long id) {
        parameterService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<BasicAdminParameterResponse> putAdParameter(
            Long id, BasicAdminParameter basicAdminParameter) {
        return ResponseEntity.ok(parameterService.update(id, basicAdminParameter));
    }
}
