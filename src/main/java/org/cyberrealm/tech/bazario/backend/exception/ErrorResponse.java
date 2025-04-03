package org.cyberrealm.tech.bazario.backend.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record ErrorResponse(
        String message,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")LocalDateTime timestamp) {
}
