package org.cyberrealm.tech.bazario.backend.exception.custom;

import org.springframework.http.HttpStatus;

public class ArgumentNotValidException extends BasicApplicationException {

    public ArgumentNotValidException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
