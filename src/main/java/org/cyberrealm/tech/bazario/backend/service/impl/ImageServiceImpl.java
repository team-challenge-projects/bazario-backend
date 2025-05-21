package org.cyberrealm.tech.bazario.backend.service.impl;

import java.net.URI;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.dto.TypeImage;
import org.cyberrealm.tech.bazario.backend.exception.custom.ArgumentNotValidException;
import org.cyberrealm.tech.bazario.backend.exception.custom.EntityNotFoundException;
import org.cyberrealm.tech.bazario.backend.exception.custom.ForbiddenException;
import org.cyberrealm.tech.bazario.backend.exception.custom.NotFoundResourceException;
import org.cyberrealm.tech.bazario.backend.model.Ad;
import org.cyberrealm.tech.bazario.backend.model.Category;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.model.enums.Role;
import org.cyberrealm.tech.bazario.backend.repository.UserRepository;
import org.cyberrealm.tech.bazario.backend.service.AccessAdService;
import org.cyberrealm.tech.bazario.backend.service.AuthenticationUserService;
import org.cyberrealm.tech.bazario.backend.service.CategoryService;
import org.cyberrealm.tech.bazario.backend.service.FileUpload;
import org.cyberrealm.tech.bazario.backend.service.ImageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    public static final String SEPARATOR_NAME_AND_IMAGE = "_";
    public static final String BLANK_VALUE = "";
    public static final String SEPARATOR_NAME_AND_EXTENSION = ".";

    @Value("${image.max-num}")
    private int maxNumImages;

    private final AccessAdService adService;
    private final AuthenticationUserService authUserService;
    private final UserRepository userRepository;
    private final CategoryService categoryService;
    private final FileUpload fileUpload;

    @Override
    public String save(TypeImage type, Long id, MultipartFile file) {
        return switch (type) {
            case AD -> addToAd(id, file);
            case AVATAR -> addToUser(id, file);
            case CATEGORY -> addToCategory(id, file);
        };
    }

    private String addToCategory(Long id, MultipartFile file) {
        var category = categoryService.get(id);
        return saveAndGetCategoryImage(file, category);
    }

    public String addToAd(Long id, MultipartFile file) {
        var ad = adService.getProtectedAd(id);
        var images = ad.getImages();

        if (images.size() > maxNumImages) {
            throw new ForbiddenException("Size of images is too large");
        }

        return getAndSaveToAd(file, images, ad);
    }

    private String addToUser(Long id, MultipartFile file) {
        return getAndSaveToUser(file, getUser(id));
    }

    @Override
    public String change(TypeImage type, Long id, URI oldValue, MultipartFile file) {
        return switch (type) {
            case AD -> changeToAd(id, oldValue, file);
            case AVATAR -> changeToUser(id, oldValue, file);
            case CATEGORY -> changeToCategory(id, oldValue, file);
        };
    }

    private String changeToCategory(Long id, URI oldValue, MultipartFile file) {
        var category = categoryService.get(id);
        if (!category.getImage().equals(oldValue.toString())) {
            throw new NotFoundResourceException("Old url %s is not found".formatted(id));
        }
        deleteFile(oldValue);
        return saveAndGetCategoryImage(file, category);
    }

    private String changeToAd(Long id, URI oldValue, MultipartFile file) {
        var ad = adService.getProtectedAd(id);
        var images = ad.getImages();
        if (!images.removeIf(url -> url.equals(oldValue.toString()))) {
            throw new ArgumentNotValidException("Not found url: %s of ad with id %d"
                    .formatted(oldValue.toString(), id));
        }
        deleteFile(oldValue);
        return getAndSaveToAd(file, images, ad);
    }

    private String changeToUser(Long id, URI oldValue, MultipartFile file) {
        var user = getUser(id);
        deleteFile(oldValue);
        return getAndSaveToUser(file, user);
    }

    @Override
    public void delete(TypeImage type, Long id, URI url) {
        switch (type) {
            case AD -> deleteFromAd(id, url);
            case AVATAR -> deleteFromUser(id, url);
            case CATEGORY -> deleteFromCategory(id, url);
            default -> throw new ArgumentNotValidException("Not correct type");
        }
    }

    private void deleteFromCategory(Long id, URI url) {
        var category = categoryService.get(id);
        deleteFile(url);
        category.setImage(BLANK_VALUE);
        categoryService.save(category);
    }

    private void deleteFromUser(Long id, URI url) {
        var user = getUser(id);
        deleteFile(url);
        user.setAvatar(BLANK_VALUE);
        userRepository.save(user);
    }

    private void deleteFromAd(Long id, URI url) {
        var ad = adService.getProtectedAd(id);
        var images = ad.getImages();
        if (!images.removeIf(value -> value.equals(url.toString()))) {
            throw new ArgumentNotValidException("Not found url: %s of ad with id %d"
                    .formatted(url.toString(), id));
        }
        deleteFile(url);
        ad.setImages(images);
        adService.save(ad);
    }

    @Override
    public void deleteFile(URI oldValue) {
        String fileName = Path.of(oldValue.getPath()).getFileName().toString();
        fileUpload.deleteFile(getFileNameWithoutExtension(fileName));
    }

    private static String getFileNameWithoutExtension(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new ArgumentNotValidException("File name is not null or blank");
        }
        var lastDotIndex = fileName.indexOf(SEPARATOR_NAME_AND_EXTENSION);
        return fileName.substring(0, lastDotIndex);
    }

    private String saveAndGetCategoryImage(MultipartFile file, Category category) {
        var url = fileUpload.uploadFile(file, createKey(file));
        category.setImage(url);
        categoryService.save(category);
        return url;
    }

    private User getUser(Long id) {
        var currentUser = authUserService.getCurrentUser();
        return (currentUser.getRole().equals(Role.ROOT)
                || currentUser.getRole().equals(Role.ADMIN))
                && !currentUser.getId().equals(id)
                ? userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("User not found"))
                : currentUser;
    }

    private String getAndSaveToAd(MultipartFile file, Set<String> images, Ad ad) {
        var url = fileUpload.uploadFile(file, createKey(file));
        images.add(url);
        ad.setImages(images);
        adService.save(ad);
        return url;
    }

    private String getAndSaveToUser(MultipartFile file, User user) {
        var url = fileUpload.uploadFile(file, createKey(file));
        user.setAvatar(url);
        userRepository.save(user);
        return url;
    }

    private static String createKey(MultipartFile file) {
        return "%s%s%s".formatted(UUID.randomUUID(), SEPARATOR_NAME_AND_IMAGE,
                getFileNameWithoutExtension(Optional.ofNullable(file.getOriginalFilename())
                        .orElseThrow(() -> new ArgumentNotValidException(
                                "File name is not null"))));
    }
}
