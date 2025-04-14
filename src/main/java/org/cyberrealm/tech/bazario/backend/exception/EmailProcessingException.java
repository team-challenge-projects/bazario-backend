package org.cyberrealm.tech.bazario.backend.exception;

public class EmailProcessingException extends RuntimeException {
    public EmailProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
