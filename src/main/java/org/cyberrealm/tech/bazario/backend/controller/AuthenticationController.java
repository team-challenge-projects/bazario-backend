//package org.cyberrealm.tech.bazario.backend.controller;
//
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.cyberrealm.tech.bazario.backend.dto.AccessTokenResponseDto;
//import org.cyberrealm.tech.bazario.backend.dto.UserForgotPasswordRequestDto;
//import org.cyberrealm.tech.bazario.backend.dto.UserLoginRequestDto;
//import org.cyberrealm.tech.bazario.backend.dto.UserLoginResponseDto;
//import org.cyberrealm.tech.bazario.backend.dto.UserRegistrationRequestDto;
//import org.cyberrealm.tech.bazario.backend.dto.UserResponseDto;
//import org.cyberrealm.tech.bazario.backend.dto.UserVerifyPasswordResetTokenRequestDto;
//import org.cyberrealm.tech.bazario.backend.exception.RegistrationException;
//import org.cyberrealm.tech.bazario.backend.security.AuthenticationService;
//import org.cyberrealm.tech.bazario.backend.security.CookieService;
//import org.cyberrealm.tech.bazario.backend.service.UserService;
//import org.cyberrealm.tech.bazario.backend.service.impl.PasswordResetService;
//import org.springframework.web.bind.annotation.CookieValue;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@Tag(name = "Authentication management", description =
//        "Endpoints for managing users registration")
//@RequiredArgsConstructor
//@RestController
//@RequestMapping("/auth")
//public class AuthenticationController {
//    private final UserService userService;
//    private final AuthenticationService authenticationService;
//    private final CookieService cookieService;
//    private final PasswordResetService passwordResetService;
//
//    @PostMapping("/registration")
//    @Operation(summary = "Register a new user",
//            description = "Register a new user by providing their details such as username, "
//                    + "password, and email. ")
//    public UserResponseDto register(@RequestBody @Valid UserRegistrationRequestDto requestDto)
//            throws RegistrationException {
//        return userService.register(requestDto);
//    }
//
//    @PostMapping("/login")
//    @Operation(summary = "Authenticate user",
//            description = "Login an existing user by validating their username and password. ")
//    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto request,
//                                      HttpServletResponse response) {
//        UserLoginResponseDto loginResponse = authenticationService.authenticate(request);
//        cookieService.addRefreshTokenCookie(loginResponse.refreshToken(), response);
//        return loginResponse;
//    }
//
//    @PostMapping("/refresh")
//    @Operation(summary = "Refresh access token",
//            description = "Create new access token by refresh token")
//    public AccessTokenResponseDto refresh(@CookieValue("refreshToken") String refreshToken) {
//        return authenticationService.refreshAccessToken(refreshToken);
//    }
//
//    @GetMapping("/logout")
//    @Operation(summary = "Logout from user account",
//            description = "Clear refresh token cookies")
//    public void logout(HttpServletResponse response) {
//        cookieService.clearRefreshTokenCookie(response);
//    }
//
//    @PostMapping("/forgot-password")
//    public void forgotPassword(@RequestBody @Valid UserForgotPasswordRequestDto requestDto) {
//        passwordResetService.generatePasswordResetCode(requestDto.email());
//    }
//
//    @PostMapping("/verify-reset-code")
//    public boolean verifyResetCode(
//            @RequestBody @Valid UserVerifyPasswordResetTokenRequestDto requestDto) {
//        return passwordResetService.verifyPasswordResetToken(requestDto.token());
//    }
//}
