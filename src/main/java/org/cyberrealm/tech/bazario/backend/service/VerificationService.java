package org.cyberrealm.tech.bazario.backend.service;

import org.cyberrealm.tech.bazario.backend.dto.VerificationEmail;

public interface VerificationService {
    boolean verifyToken(VerificationEmail verificationEmail);

    void markVerified(String email);
}
