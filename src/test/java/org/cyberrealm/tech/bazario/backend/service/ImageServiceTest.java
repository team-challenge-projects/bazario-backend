package org.cyberrealm.tech.bazario.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.cyberrealm.tech.bazario.backend.dto.TypeImage;
import org.cyberrealm.tech.bazario.backend.model.Ad;
import org.cyberrealm.tech.bazario.backend.model.Category;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.model.enums.Role;
import org.cyberrealm.tech.bazario.backend.repository.UserRepository;
import org.cyberrealm.tech.bazario.backend.service.impl.ImageServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ImageServiceTest {
    private static final long ONE_ID = 1L;
    private static final String HTTP = "http";
    private static final long TWO_ID = 2L;
    private static final String OLD_FILE = "oldValue.jpg";
    private static final String OLD_NAME_FILE = "oldValue";

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
    private MultipartFile mockMultipart;

    void setUp() throws IOException {
        mockMultipart = new MockMultipartFile("file.jpg", "file.jpg",
                "application/json", InputStream.nullInputStream());

        when(fileUpload.uploadFile(eq(mockMultipart), anyString())).thenReturn(HTTP);
    }

    @Test
    void saveToAd() throws IOException {
        setUp();
        ReflectionTestUtils.setField(imageService, "maxNumImages", 6);
        var ad = new Ad();
        ad.setImages(new HashSet<>());
        ad.setId(ONE_ID);
        when(adService.getProtectedAd(ONE_ID)).thenReturn(ad);

        assertEquals(HTTP, imageService.save(TypeImage.AD, ONE_ID, mockMultipart));
        assertEquals(1, ad.getImages().size());
        assertTrue(ad.getImages().contains(HTTP));
        verify(adService).save(ad);
    }

    @Test
    void saveToUser() throws IOException {
        setUp();
        var currentUser = new User();
        currentUser.setId(TWO_ID);
        currentUser.setRole(Role.ADMIN);
        var user = new User();
        user.setId(ONE_ID);
        user.setRole(Role.USER);
        when(authUserService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findById(ONE_ID)).thenReturn(Optional.of(user));

        assertEquals(HTTP, imageService.save(TypeImage.AVATAR, ONE_ID, mockMultipart));
        assertEquals(HTTP, user.getAvatar());
        verify(userRepository).save(user);
    }

    @Test
    void saveToCategory() throws IOException {
        setUp();
        var category = new Category();
        category.setId(ONE_ID);
        when(categoryService.get(ONE_ID)).thenReturn(category);

        assertEquals(HTTP, imageService.save(TypeImage.CATEGORY, ONE_ID, mockMultipart));
        assertEquals(HTTP, category.getImage());
        verify(categoryService).save(category);
    }

    @Test
    void changeToAd() throws IOException {
        setUp();
        var ad = new Ad();
        ad.setImages(new HashSet<>(Set.of(OLD_FILE)));
        ad.setId(ONE_ID);
        when(adService.getProtectedAd(ONE_ID)).thenReturn(ad);

        assertEquals(HTTP, imageService.change(TypeImage.AD, ONE_ID,
                URI.create(OLD_FILE), mockMultipart));
        assertTrue(ad.getImages().contains(HTTP));
        verify(fileUpload).deleteFile(OLD_NAME_FILE);
        verify(adService).save(ad);
    }

    @Test
    void changeToUser() throws IOException {
        setUp();
        var currentUser = new User();
        currentUser.setId(TWO_ID);
        currentUser.setRole(Role.ADMIN);
        var user = new User();
        user.setId(ONE_ID);
        user.setRole(Role.USER);
        when(authUserService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findById(ONE_ID)).thenReturn(Optional.of(user));

        assertEquals(HTTP, imageService.change(TypeImage.AVATAR, ONE_ID,
                URI.create(OLD_FILE), mockMultipart));
        assertEquals(HTTP, user.getAvatar());
        verify(fileUpload).deleteFile(OLD_NAME_FILE);
        verify(userRepository).save(user);
    }

    @Test
    void changeToCategory() throws IOException {
        setUp();
        var category = new Category();
        category.setId(ONE_ID);
        category.setImage(OLD_FILE);
        when(categoryService.get(ONE_ID)).thenReturn(category);

        assertEquals(HTTP, imageService.change(TypeImage.CATEGORY, ONE_ID,
                URI.create(OLD_FILE), mockMultipart));
        assertEquals(HTTP, category.getImage());
        verify(fileUpload).deleteFile(OLD_NAME_FILE);
        verify(categoryService).save(category);
    }

    @Test
    void deleteToAd() throws IOException {
        var ad = new Ad();
        ad.setImages(new HashSet<>(Set.of(OLD_FILE)));
        ad.setId(ONE_ID);
        when(adService.getProtectedAd(ONE_ID)).thenReturn(ad);

        imageService.delete(TypeImage.AD, ONE_ID, URI.create(OLD_FILE));

        assertTrue(ad.getImages().isEmpty());
        verify(fileUpload).deleteFile(OLD_NAME_FILE);
        verify(adService).save(ad);
    }

    @Test
    void deleteToUser() {
        var currentUser = new User();
        currentUser.setId(TWO_ID);
        currentUser.setRole(Role.ADMIN);
        var user = new User();
        user.setId(ONE_ID);
        user.setRole(Role.USER);
        user.setAvatar(OLD_FILE);
        when(authUserService.getCurrentUser()).thenReturn(currentUser);
        when(userRepository.findById(ONE_ID)).thenReturn(Optional.of(user));

        imageService.delete(TypeImage.AVATAR, ONE_ID, URI.create(OLD_FILE));

        assertTrue(user.getAvatar().isBlank());
        verify(fileUpload).deleteFile(OLD_NAME_FILE);
        verify(userRepository).save(user);
    }

    @Test
    void deleteToCategory() {
        var category = new Category();
        category.setId(ONE_ID);
        category.setImage(OLD_FILE);
        when(categoryService.get(ONE_ID)).thenReturn(category);

        imageService.delete(TypeImage.CATEGORY, ONE_ID, URI.create(OLD_FILE));

        assertTrue(category.getImage().isBlank());
        verify(fileUpload).deleteFile(OLD_NAME_FILE);
        verify(categoryService).save(category);
    }

    @Test
    void deleteFile() {
        imageService.deleteFile(URI.create(OLD_FILE));
        verify(fileUpload).deleteFile(OLD_NAME_FILE);
    }
}
