package org.cyberrealm.tech.bazario.backend.exception.custom;

import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends BasicApplicationException {
    public EntityNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
