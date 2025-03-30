package org.cyberrealm.tech.bazario.backend.exception.custom;

import org.springframework.http.HttpStatus;

public class ArgumentNotValidExeption extends BasicApplicationException {

    public ArgumentNotValidExeption(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
