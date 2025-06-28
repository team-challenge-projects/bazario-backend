package org.cyberrealm.tech.bazario.backend.api.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;
import org.cyberrealm.tech.bazario.backend.AbstractIntegrationTest;
import org.cyberrealm.tech.bazario.backend.dto.BasicParameter;
import org.cyberrealm.tech.bazario.backend.dto.EmailRequest;
import org.cyberrealm.tech.bazario.backend.dto.PrivateUserInformation;
import org.cyberrealm.tech.bazario.backend.dto.RegistrationRequest;
import org.cyberrealm.tech.bazario.backend.dto.TypeEmailMessage;
import org.cyberrealm.tech.bazario.backend.dto.UserInformation;
import org.cyberrealm.tech.bazario.backend.dto.VerificationEmail;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.model.enums.MessageType;
import org.cyberrealm.tech.bazario.backend.repository.UserRepository;
import org.cyberrealm.tech.bazario.backend.service.AuthenticationUserService;
import org.cyberrealm.tech.bazario.backend.service.EmailTemplateBuilder;
import org.cyberrealm.tech.bazario.backend.service.FileUpload;
import org.cyberrealm.tech.bazario.backend.service.TokenService;
import org.cyberrealm.tech.bazario.backend.service.VerificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class UserApiDelegateImplTest extends AbstractIntegrationTest {
    @MockitoBean
    private ValueOperations<String, Object> valueOperations;
    @MockitoBean
    private AuthenticationUserService authService;
    @Autowired
    private UserRepository userRepository;
    @MockitoBean
    private FileUpload fileUpload;
    @MockitoBean
    private TokenService tokenService;
    @MockitoBean
    private EmailTemplateBuilder templateBuilder;
    @MockitoBean
    private GeoOperations<String, Object> opsForGeo;

    @Test
    void createUser() throws Exception {
        var dto = new RegistrationRequest().email("new-test@test.com")
                .firstName("Test").password("tesT2#test")
                .phoneNumber("+380670001234");
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations).set(anyString(),any(), any(Duration.class));

        mockMvc.perform(post("/anonymous/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    @Rollback
    void deleteUser() throws Exception {
        var user = userRepository.findById(ID_ONE).orElseThrow();
        entityManager.clear();
        when(authService.getCurrentUser()).thenReturn(user);
        mockMvc.perform(delete("/private/user"))
                .andExpect(status().isNoContent());
        var deleteUser = userRepository.findById(ID_ONE).orElseThrow();
        assertAll(
                () -> assertTrue(deleteUser.isLocked()),
                () -> assertTrue(deleteUser.getEmail().startsWith("delete_"))
        );
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @Rollback
    void deleteUserByAdmin() throws Exception {
        mockMvc.perform(delete("/admin/user/" + ID_ONE))
                .andExpect(status().isNoContent());
        assertThrows(NoSuchElementException.class, () ->
                userRepository.findById(ID_ONE).orElseThrow());
    }

    @Test
    @WithMockUser
    void getOtherUserInformation() throws Exception {
        var user = new User();
        user.setId(ID_ONE);
        var dto = new UserInformation().id(ID_ONE).email("test@test.com")
                .phoneNumber("+380671234567").firstName("Тест")
                .lastName("Тест").avatar("http://test/test.png")
                .cityName("Kiev").distance(1.0);
        when(redisTemplate.opsForGeo()).thenReturn(opsForGeo);
        when(authService.getCurrentUser()).thenReturn(user);
        when(opsForGeo.distance(anyString(), anyString(), anyString()))
                .thenReturn(new Distance(1.0));
        mockMvc.perform(get("/private/user/" + ID_ONE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    @WithMockUser
    void getUserInformation() throws Exception {
        var user = userRepository.findById(ID_ONE).orElseThrow();
        when(authService.getCurrentUser()).thenReturn(user);

        var dto = new PrivateUserInformation().id(ID_ONE).email("test@test.com")
                .phoneNumber("+380671234567").firstName("Тест")
                .lastName("Тест").avatar("http://test/test.png")
                .cityName("Kiev").cityCoordinate("50,27|30,3125")
                .parameters(List.of(new BasicParameter().id(ID_ONE)
                        .name("Тестовий тип").parameterId(ID_ONE)
                        .parameterValue("ТестТип")
                        .restrictionPattern("^(ТестТип|ЮридичнийТип)$")
                        .descriptionPattern("Це тестовий тип")));
        mockMvc.perform(get("/private/user"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @ParameterizedTest
    @EnumSource(TypeEmailMessage.class)
    void sendMessage(TypeEmailMessage type) throws Exception {
        String email = "test@test.com";
        String html = "html";

        when(tokenService.generateToken(eq(email),any(MessageType.class)))
                .thenReturn("token");
        when(templateBuilder.buildEmailVerificationEmail(anyInt(), anyString()))
                .thenReturn(html);
        when(templateBuilder.buildPasswordResetEmail(anyInt(), anyString()))
                .thenReturn(html);

        var dto = new EmailRequest().email(email);
        mockMvc.perform(post("/public/send/{type}", type)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());

        verify(emailSender).sendEmail(eq(email), anyString(), eq(html));
    }

    @WithMockUser
    @Test
    void updateUser() throws Exception {
        var user = userRepository.findById(ID_ONE).orElseThrow();
        when(authService.getCurrentUser()).thenReturn(user);
        var dto = new PrivateUserInformation()
                .cityCoordinate("50,27|30,3125")
                .parameters(List.of()).email("test@test.com")
                .phoneNumber("+380671234567").id(ID_ONE)
                .firstName("Change").lastName("Тест")
                .avatar("http://test/test.png")
                .cityName("Kiev");
        mockMvc.perform(patch("/private/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\": \"Change\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper
                        .writeValueAsString(dto)));
    }

    @WithMockUser(roles = {"ADMIN"})
    @Test
    void updateUserByAdmin() throws Exception {
        var user = userRepository.findById(ID_TWO).orElseThrow();
        when(authService.getCurrentUser()).thenReturn(user);

        var dto = new PrivateUserInformation()
                .cityCoordinate("50,27|30,3125")
                .parameters(List.of()).email("test@test.com")
                .phoneNumber("+380671234567").id(ID_ONE)
                .firstName("Change").lastName("Тест")
                .avatar("http://test/test.png")
                .cityName("Kiev");
        mockMvc.perform(patch("/admin/user/" + ID_ONE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\": \"Change\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper
                        .writeValueAsString(dto)));
    }

    @Test
    void verifyEmail() throws Exception {
        String email = "new-test@test.com";
        String hex = "some hex";
        when(tokenService.verifyToken(hex, email,
                MessageType.EMAIL_VERIFICATION)).thenReturn(true);

        var registration = new RegistrationRequest().email(email)
                .phoneNumber("+380670001234").firstName("Johan")
                .password("johaN1#johan");
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.getAndDelete(email
                + VerificationService.EMAIL_VERIFICATION_KEY_SUFFIX))
                .thenReturn(objectMapper.writeValueAsString(registration));

        var dto = new VerificationEmail().email(email)
                .hex(hex);
        mockMvc.perform(post("/anonymous/email/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
        var newUser = userRepository.findByEmail(email).orElseThrow();
        assertAll(
                () -> assertEquals(registration.getFirstName(),
                        newUser.getFirstName()),
                () -> assertEquals(registration.getPhoneNumber(),
                        newUser.getPhoneNumber())
        );
    }
}
