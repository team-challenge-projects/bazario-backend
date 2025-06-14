package org.cyberrealm.tech.bazario.backend.api.impl;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.util.UUID;
import org.cyberrealm.tech.bazario.backend.AbstractIntegrationTest;
import org.cyberrealm.tech.bazario.backend.dto.TypeImage;
import org.cyberrealm.tech.bazario.backend.repository.UserRepository;
import org.cyberrealm.tech.bazario.backend.service.AuthenticationUserService;
import org.cyberrealm.tech.bazario.backend.service.FileUpload;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class ImageApiDelegateImplTest extends AbstractIntegrationTest {
    @MockitoBean
    private FileUpload fileUpload;
    @MockitoBean
    private AuthenticationUserService authService;
    @Autowired
    private UserRepository userRepository;

    @WithMockUser
    @ParameterizedTest
    @EnumSource(TypeImage.class)
    void changeImage(TypeImage type) throws Exception {
        var nameFile = "test-image.png";
        var mockFile = getMockFile(nameFile);

        UUID expectedUuid = UUID.fromString(
                "00000000-0000-0000-0000-000000000001");
        var expectedUrl = "http://test/image/%s_%s"
                .formatted(expectedUuid, nameFile);
        when(authService.isAuthenticationUser()).thenReturn(true);
        when(fileUpload.uploadFile(mockFile, "%s_test-image".formatted(expectedUuid)))
                .thenReturn(expectedUrl);

        var user = userRepository.findById(ID_ONE).orElseThrow();
        when(authService.getCurrentUser()).thenReturn(user);
        try (var mockUuid = mockStatic(UUID.class)) {
            mockUuid.when(UUID::randomUUID).thenReturn(expectedUuid);

            mockMvc.perform(multipart("/image/{type}/{id}",
                            type, ID_ONE)
                            .file(mockFile)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .with(request -> {
                                request.setMethod("PUT");
                                request.addParameter("oldValue", "http://test/test.png");
                                return request;
                            }))
                    .andExpect(status().isOk())
                    .andExpect(content().json(objectMapper
                            .writeValueAsString(URI.create(expectedUrl))));
        }
    }

    @WithMockUser
    @ParameterizedTest
    @EnumSource(TypeImage.class)
    void deleteImage(TypeImage type) throws Exception {
        var user = userRepository.findById(ID_ONE).orElseThrow();
        when(authService.getCurrentUser()).thenReturn(user);
        when(authService.isAuthenticationUser()).thenReturn(true);
        mockMvc.perform(delete("/image/{type}/{id}", type, ID_ONE)
                        .param("url", type.equals(TypeImage.AD)
                                ? "http://test/old-test.png" : "http://test/test.png"))
                .andExpect(status().isNoContent());
    }

    @WithMockUser
    @ParameterizedTest
    @EnumSource(TypeImage.class)
    void saveImage(TypeImage type) throws Exception {
        var nameFile = "test-image.png";
        var mockFile = getMockFile(nameFile);

        UUID expectedUuid = UUID.fromString(
                "00000000-0000-0000-0000-000000000001");
        var expectedUrl = "http://test/image/%s_%s"
                .formatted(expectedUuid, nameFile);
        when(authService.isAuthenticationUser()).thenReturn(true);
        when(fileUpload.uploadFile(mockFile, "%s_test-image".formatted(expectedUuid)))
                .thenReturn(expectedUrl);

        var user = userRepository.findById(ID_ONE).orElseThrow();
        when(authService.getCurrentUser()).thenReturn(user);

        try (var mockUuid = mockStatic(UUID.class)) {
            mockUuid.when(UUID::randomUUID).thenReturn(expectedUuid);

            mockMvc.perform(multipart("/image/{type}/{id}",
                            type, ID_ONE)
                            .file(mockFile)
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isCreated())
                    .andExpect(content().json(objectMapper
                            .writeValueAsString(URI.create(expectedUrl))));
        }
    }

    private static MockMultipartFile getMockFile(String nameFile) {
        var content = "some image content".getBytes();
        return new MockMultipartFile("file",
                nameFile, MediaType.IMAGE_PNG_VALUE, content);
    }
}
