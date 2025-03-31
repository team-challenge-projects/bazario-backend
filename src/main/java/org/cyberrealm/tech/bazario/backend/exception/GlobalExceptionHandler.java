package org.cyberrealm.tech.bazario.backend.exception;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.cyberrealm.tech.bazario.backend.exception.custom.BasicApplicationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String DELIMITER_KEY_VALUE = "=";
    private static final String DELIMITER_ERRORS = ", ";
    private static final String PREFIX_ARRAY_ERRORS = "{";
    private static final String SUFFIX_ARRAY_ERRORS = "}";

    /**
     * Handles custom application exceptions and logs the error
     * before returning an error response.
     *
     * @param ex The custom application exception to handle.
     * @return A ResponseEntity containing an error response
     *         with the exception message and timestamp.
     */
    @ExceptionHandler(BasicApplicationException.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Entity not found")
    })
    public ResponseEntity<ErrorResponse> handleCustomException(final BasicApplicationException ex) {
        log.error(ex.getClass().getSimpleName(), ex.getMessage());

        ErrorResponse response = new ErrorResponse(ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(ex.getHttpStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ApiResponse(responseCode = "400", description = "Invalid Input")
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Function<ObjectError, String> errorStringFunction = error ->
                ((FieldError) error).getField() + DELIMITER_KEY_VALUE
                        + Optional.ofNullable(error.getDefaultMessage())
                                .orElse("Not message");
        String errors = ex.getBindingResult().getAllErrors().stream()
                    .map(errorStringFunction).collect(Collectors.joining(
                            DELIMITER_ERRORS, PREFIX_ARRAY_ERRORS,SUFFIX_ARRAY_ERRORS));

        log.error(ex.getClass().getSimpleName(), errors);
        ErrorResponse response = new ErrorResponse(errors, LocalDateTime.now());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ApiResponse(responseCode = "413", description = "Payload too large")
    public ResponseEntity<ErrorResponse> handleMaxSizeException(
            MaxUploadSizeExceededException ex) {
        String message = "The file size is too large. Maximum file size allowed:"
                + ex.getMaxUploadSize() + " bytes";
        log.error(ex.getClass().getSimpleName(), message);
        ErrorResponse response = new ErrorResponse(message, LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(response);
    }

    /**
     * Handles server exceptions and logs the error
     * before returning an internal server error response.
     *
     * @param ex The server exception to handle.
     * @return A ResponseEntity containing an error response
     *         with the exception message and timestamp.
     */
    @ExceptionHandler(Exception.class)
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    public ResponseEntity<ErrorResponse> handleServerException(final Exception ex) {
        log.error(ex.getClass().getSimpleName(), ex.getMessage());

        ErrorResponse response = new ErrorResponse(ex.getMessage(), LocalDateTime.now());
        return ResponseEntity.internalServerError().body(response);
    }
}