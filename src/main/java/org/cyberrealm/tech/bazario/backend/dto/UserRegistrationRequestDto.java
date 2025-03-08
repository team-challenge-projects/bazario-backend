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
        @ValidEmail
        String email,
        @NotBlank
        @Size(min = 8, max = 20)
        String password,
        @NotBlank
        @Size(min = 8, max = 20)
        String repeatPassword,
        @NotBlank
        String firstName,
        @NotBlank
        String lastName,
        String phoneNumber,
        String city
) {
}
