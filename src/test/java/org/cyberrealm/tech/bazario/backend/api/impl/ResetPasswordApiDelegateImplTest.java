package org.cyberrealm.tech.bazario.backend.api.impl;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.cyberrealm.tech.bazario.backend.dto.ResetPassword;
import org.cyberrealm.tech.bazario.backend.service.EmailSender;
import org.cyberrealm.tech.bazario.backend.service.impl.EmailNotificationService;
import org.cyberrealm.tech.bazario.backend.service.impl.PasswordResetService;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class ResetPasswordApiDelegateImplTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private PasswordResetService passwordResetService;
    @MockitoBean
    private EmailNotificationService emailService;
    @MockitoBean
    private EmailSender emailSender;

    @Test
    void resetPassword() throws Exception {
        var reset = new ResetPassword();
        reset.setEmail(JsonNullable.of("vitalii@ukr.net"));
        reset.setPassword(JsonNullable.of("safwecQsc#sdvewQ"));
        reset.setHex(null);
        mockMvc.perform(post("/api/public/resetPassword")
                        .requestAttr("resetPassword", reset))
                .andExpect(status().isUnauthorized());
    }
}
