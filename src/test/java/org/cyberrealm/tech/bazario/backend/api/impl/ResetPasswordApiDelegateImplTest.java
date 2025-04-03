//package org.cyberrealm.tech.bazario.backend.api.impl;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import org.cyberrealm.tech.bazario.backend.dto.ResetPassword;
//import org.cyberrealm.tech.bazario.backend.service.impl.EmailService;
//import org.cyberrealm.tech.bazario.backend.service.impl.PasswordResetService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//@SpringBootTest
//@ActiveProfiles("test")
//@AutoConfigureMockMvc
//class ResetPasswordApiDelegateImplTest {
//    @Autowired
//    private MockMvc mockMvc;
//    @MockitoBean
//    private PasswordResetService passwordResetService;
//    @MockitoBean
//    private EmailService emailService;
//
//    @Test
//    void resetPassword() throws Exception {
//        var reset = new ResetPassword();
//        mockMvc.perform(post("/api/public/resetPassword")
//                        .requestAttr("resetPassword", reset))
//                .andExpect(status().isNoContent());
//    }
//}
