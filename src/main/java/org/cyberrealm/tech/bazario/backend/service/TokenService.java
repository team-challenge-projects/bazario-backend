package org.cyberrealm.tech.bazario.backend.service;

import org.cyberrealm.tech.bazario.backend.model.enums.MessageType;

public interface TokenService {
    /**
     * Generate a token for verification email or reset password
     *
     * @author Andrey Sitarskiy
     * @param email User email
     * @param messageType essentials of send email
     */
    String generateToken(String email, MessageType messageType);

    /**
     * Verify a token for verification email or reset password
     *
     * @author Andrey Sitarskiy
     * @param token Accepted token
     * @param email User email
     * @param messageType the source of the received e-mail
     */
    boolean verifyToken(String token, String email, MessageType messageType);
}
