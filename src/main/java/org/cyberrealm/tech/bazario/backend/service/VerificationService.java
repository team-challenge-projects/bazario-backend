package org.cyberrealm.tech.bazario.backend.service;

import org.cyberrealm.tech.bazario.backend.dto.VerificationEmail;

public interface VerificationService {
    String EMAIL_VERIFICATION_KEY_SUFFIX = ":EMAIL_VERIFICATION_DTO";
    String CHANGE_EMAIL_DTO_SUFFIX = ":CHANGE_EMAIL_DTO";

    /**
     * Check verify token
     *
     * @author Vitalii Pavlyk
     * @author Andrey Sitarskiy
     * @param verificationEmail Dto contains email and token
     * @return verify token
     */
    boolean verifyToken(VerificationEmail verificationEmail);

    /**
     * Registration user or change email.
     * Get dto from cache and write in database.
     * If a user deletes himself or herself during
     * re-registration, we will restore it.
     *
     * @author Vitalii Pavlyk
     * @author Andrey Sitarskiy
     * @param email Key for cache
     */
    void markVerified(String email);
}
