package org.cyberrealm.tech.bazario.backend.service;

import org.cyberrealm.tech.bazario.backend.model.enums.MessageType;

public interface TokenService {
    String generateToken(String email, MessageType messageType);

    boolean verifyToken(String token, String email, MessageType messageType);
}
