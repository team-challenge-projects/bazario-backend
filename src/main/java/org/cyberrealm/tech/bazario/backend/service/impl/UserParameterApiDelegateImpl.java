package org.cyberrealm.tech.bazario.backend.service.impl;

import org.cyberrealm.tech.bazario.backend.api.UserParameterApiDelegate;
import org.cyberrealm.tech.bazario.backend.dto.BasisAdminParameter;
import org.springframework.http.ResponseEntity;

public class UserParameterApiDelegateImpl implements UserParameterApiDelegate {
    @Override
    public ResponseEntity<Void> createUserParameter(BasisAdminParameter basisAdminParameter) {
        return UserParameterApiDelegate.super.createUserParameter(basisAdminParameter);
    }

    @Override
    public ResponseEntity<Void> deleteUserParameter(Long id) {
        return UserParameterApiDelegate.super.deleteUserParameter(id);
    }

    @Override
    public ResponseEntity<Void> putUserParameter(Long id, BasisAdminParameter basisAdminParameter) {
        return UserParameterApiDelegate.super.putUserParameter(id, basisAdminParameter);
    }
}
