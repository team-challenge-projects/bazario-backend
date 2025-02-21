package org.cyberrealm.tech.bazario.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.UserLoginRequestDto;
import org.cyberrealm.tech.bazario.backend.dto.UserLoginResponseDto;
import org.cyberrealm.tech.bazario.backend.dto.UserRegistrationRequestDto;
import org.cyberrealm.tech.bazario.backend.dto.UserResponseDto;
import org.cyberrealm.tech.bazario.backend.exception.RegistrationException;
import org.cyberrealm.tech.bazario.backend.security.AuthenticationService;
import org.cyberrealm.tech.bazario.backend.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication management", description = "Endpoints for managing users registration")
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/registration")
    @Operation(summary = "Register a new user",
            description = "Register a new user by providing their details such as username, "
                    + "password, and email. ")
    public UserResponseDto register(@RequestBody @Valid UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        return userService.register(requestDto);
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user",
            description = "Login an existing user by validating their username and password. ")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto request) {
        return authenticationService.authenticate(request);
    }
}
