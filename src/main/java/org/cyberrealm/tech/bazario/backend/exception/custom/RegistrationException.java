package org.cyberrealm.tech.bazario.backend.exception.custom;

import org.springframework.http.HttpStatus;

public class RegistrationException extends BasicApplicationException {
    public RegistrationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
