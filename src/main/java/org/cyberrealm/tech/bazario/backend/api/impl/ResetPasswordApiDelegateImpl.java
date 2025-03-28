package org.cyberrealm.tech.bazario.backend.api.impl;

import org.cyberrealm.tech.bazario.backend.api.ResetPasswordApiDelegate;
import org.cyberrealm.tech.bazario.backend.dto.ResetPassword;
import org.springframework.http.ResponseEntity;

public class ResetPasswordApiDelegateImpl implements ResetPasswordApiDelegate {
    @Override
    public ResponseEntity<Void> resetPassword(ResetPassword resetPassword) {
        return ResponseEntity.noContent().build();
    }
}
