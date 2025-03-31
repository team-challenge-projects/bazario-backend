package org.cyberrealm.tech.bazario.backend.exception.custom;

import org.springframework.http.HttpStatus;

public class NotFoundResourceException extends BasicApplicationException {
    public NotFoundResourceException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
