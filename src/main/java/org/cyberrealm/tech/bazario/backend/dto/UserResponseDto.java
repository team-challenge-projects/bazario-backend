package org.cyberrealm.tech.bazario.backend.dto;

public record UserResponseDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        String shippingAddress
) {
}
