package org.cyberrealm.tech.bazario.backend.dto;

import jakarta.validation.constraints.NotBlank;
import org.cyberrealm.tech.bazario.backend.validation.ValidEmail;
import org.hibernate.validator.constraints.Length;

public record UserLoginRequestDto(
        @NotBlank
        @Length(min = 8, max = 60)
        @ValidEmail
        String email,
        @NotBlank
        @Length(min = 4, max = 60)
        String password
) {
}
