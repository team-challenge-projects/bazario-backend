package org.cyberrealm.tech.bazario.backend.api.impl;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.cyberrealm.tech.bazario.backend.AbstractIntegrationTest;
import org.cyberrealm.tech.bazario.backend.dto.ResetPassword;
import org.cyberrealm.tech.bazario.backend.model.enums.MessageType;
import org.cyberrealm.tech.bazario.backend.service.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class ResetPasswordApiDelegateImplTest extends AbstractIntegrationTest {
    public static final String HEX = "some hex";
    public static final String EMAIL = "test@test.com";
    @MockitoBean
    private TokenService tokenService;

    @Test
    void resetPassword() throws Exception {
        var reset = new ResetPassword().email(EMAIL)
                .password("tesT1#test").hex(HEX);
        when(tokenService.verifyToken(HEX, EMAIL, MessageType.PASSWORD_RESET))
                .thenReturn(true);

        mockMvc.perform(post("/anonymous/resetPassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reset)))
                .andExpect(status().isNoContent());
    }
}
