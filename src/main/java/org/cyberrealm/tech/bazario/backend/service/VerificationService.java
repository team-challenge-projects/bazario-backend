package org.cyberrealm.tech.bazario.backend.service;

import org.cyberrealm.tech.bazario.backend.dto.VerificationEmail;

public interface VerificationService {
    String EMAIL_VERIFICATION_KEY_SUFFIX = ":EMAIL_VERIFICATION_DTO";
    String CHANGE_EMAIL_DTO_SUFFIX = ":CHANGE_EMAIL_DTO";

    boolean verifyToken(VerificationEmail verificationEmail);

    void markVerified(String email);
}
