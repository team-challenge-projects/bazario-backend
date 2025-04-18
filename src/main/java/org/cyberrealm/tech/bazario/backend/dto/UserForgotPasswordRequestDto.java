package org.cyberrealm.tech.bazario.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record UserForgotPasswordRequestDto(
        @NotBlank
        String email
) {
}
