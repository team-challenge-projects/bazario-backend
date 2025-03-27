package org.cyberrealm.tech.bazario.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.cyberrealm.tech.bazario.backend.validation.FieldMatch;
import org.cyberrealm.tech.bazario.backend.validation.ValidEmail;

@FieldMatch.List({
        @FieldMatch(
                field = "password",
                fieldMatch = "repeatPassword",
                message = "Passwords do not match!"
        )
})
public record UserRegistrationRequestDto(
        @NotBlank
        String firstName,
        String lastName,
        @NotBlank
        @ValidEmail
        String email,
        String phoneNumber,
        @NotBlank
        @Size(min = 8, max = 20)
        String password
) {
}
