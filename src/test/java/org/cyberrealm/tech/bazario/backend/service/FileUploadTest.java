package org.cyberrealm.tech.bazario.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.cyberrealm.tech.bazario.backend.service.impl.FileUploadImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class FileUploadTest {
    @Mock
    private Cloudinary cloudinary;
    @Mock
    private Uploader uploader;
    @InjectMocks
    private FileUploadImpl fileUpload;

    @Test
    void uploadFile() throws IOException {
        when(cloudinary.uploader()).thenReturn(uploader);
        var file = new MockMultipartFile("file", InputStream.nullInputStream());
        when(uploader.upload(file.getBytes(), Map.of("public_id", "key")))
                .thenReturn(Map.of("url", "http"));

        assertEquals("http", fileUpload.uploadFile(file, "key"));
    }

    @Test
    void deleteFile() throws IOException {
        when(cloudinary.uploader()).thenReturn(uploader);

        fileUpload.deleteFile("key");

        verify(uploader).destroy("key", Map.of("invalidate", true));
    }
}
