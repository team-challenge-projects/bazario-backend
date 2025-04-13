package org.cyberrealm.tech.bazario.backend.exception.custom;

import org.springframework.http.HttpStatus;

public class PasswordResetException extends BasicApplicationException {
    public PasswordResetException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
