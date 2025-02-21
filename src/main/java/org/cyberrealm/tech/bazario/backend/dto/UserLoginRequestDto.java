package org.cyberrealm.tech.bazario.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record UserLoginRequestDto(
        @NotBlank
        @Length(min = 8, max = 60)
        @Email
        String email,
        @NotBlank
        @Length(min = 4, max = 60)
        String password
) {
}
