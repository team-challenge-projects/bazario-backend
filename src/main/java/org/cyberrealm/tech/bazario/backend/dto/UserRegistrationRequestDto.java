package org.cyberrealm.tech.bazario.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegistrationRequestDto(
        @NotBlank
        String firstName,
        String lastName,
        @NotBlank
        String email,
        String phoneNumber,
        @NotBlank
        @Size(min = 8, max = 20)
        String password
) {
}
