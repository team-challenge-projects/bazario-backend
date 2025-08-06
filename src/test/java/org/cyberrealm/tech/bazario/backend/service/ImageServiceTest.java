package org.cyberrealm.tech.bazario.backend.service;

import org.cyberrealm.tech.bazario.backend.repository.UserRepository;
import org.cyberrealm.tech.bazario.backend.service.impl.ImageServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ImageServiceTest {
    @Mock
    private AccessAdService adService;
    @Mock
    private AuthenticationUserService authUserService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CategoryService categoryService;
    @Mock
    private FileUpload fileUpload;
    @InjectMocks
    private ImageServiceImpl imageService;

    @Test
    void save() {

    }

    @Test
    void change() {
    }

    @Test
    void delete() {
    }

    @Test
    void deleteFile() {
    }
}
