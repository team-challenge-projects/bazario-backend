package org.cyberrealm.tech.bazario.backend.api.impl;

import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.api.ResetPasswordApiDelegate;
import org.cyberrealm.tech.bazario.backend.dto.ResetPassword;
import org.cyberrealm.tech.bazario.backend.service.impl.PasswordResetService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResetPasswordApiDelegateImpl implements ResetPasswordApiDelegate {
    private final PasswordResetService passwordResetService;

    @Override
    public ResponseEntity<Void> resetPassword(ResetPassword resetPassword) {
        passwordResetService.changePassword(resetPassword);
        return ResponseEntity.noContent().build();
    }
}