package org.cyberrealm.tech.bazario.backend.exception.custom;

public class TokenProcessingException extends RuntimeException {
    public TokenProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
