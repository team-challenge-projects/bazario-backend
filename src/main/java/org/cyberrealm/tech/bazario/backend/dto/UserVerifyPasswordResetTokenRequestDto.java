package org.cyberrealm.tech.bazario.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record UserVerifyPasswordResetTokenRequestDto(
        @NotBlank
        String token
) {
}
