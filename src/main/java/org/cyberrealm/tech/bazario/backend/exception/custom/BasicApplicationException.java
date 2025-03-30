package org.cyberrealm.tech.bazario.backend.exception.custom;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * This class serves as a base for creating custom exceptions
 * in the application. It extends the RuntimeException class and
 * holds information about the error's HTTP status.
 */
@Getter
public class BasicApplicationException extends RuntimeException {
    /**
     * The HTTP status associated with the exception.
     */
    private final HttpStatus httpStatus;

    /**
     * Creates a new BasicApplicationException object
     * with the specified message and HTTP status.
     *
     * @param message    Error message explaining the reason for the exception.
     * @param httpStatus The HTTP status associated with the exception.
     */
    public BasicApplicationException(final String message, final HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;

    }
}
