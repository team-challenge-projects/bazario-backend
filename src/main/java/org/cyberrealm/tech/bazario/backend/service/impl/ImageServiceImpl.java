package org.cyberrealm.tech.bazario.backend.service.impl;

import java.net.URI;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.exception.custom.ArgumentNotValidException;
import org.cyberrealm.tech.bazario.backend.exception.custom.ForbiddenException;
import org.cyberrealm.tech.bazario.backend.model.Ad;
import org.cyberrealm.tech.bazario.backend.model.User;
import org.cyberrealm.tech.bazario.backend.model.enums.Role;
import org.cyberrealm.tech.bazario.backend.service.AdService;
import org.cyberrealm.tech.bazario.backend.service.FileUpload;
import org.cyberrealm.tech.bazario.backend.service.ImageService;
import org.cyberrealm.tech.bazario.backend.service.UserService;
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

    private final AdService adService;
    private final UserService userService;
    private final FileUpload fileUpload;

    private enum TypeImage {
        AD, AVATAR
    }

    @Override
    public String save(String type, Long id, MultipartFile file) {
        return switch (getTypeImage(type)) {
            case AD -> addToAd(id, file);
            case AVATAR -> addToUser(id, file);
        };
    }

    public String addToAd(Long id, MultipartFile file) {
        var ad = adService.getAd(id);
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
    public String change(String type, Long id, URI oldValue, MultipartFile file) {
        return switch (getTypeImage(type)) {
            case AD -> changeToAd(id, oldValue, file);
            case AVATAR -> changeToUser(id, oldValue, file);
        };
    }

    private String changeToAd(Long id, URI oldValue, MultipartFile file) {
        var ad = adService.getAd(id);
        var images = ad.getImages();
        if (!images.removeIf(url -> url.equals(oldValue.toString()))) {
            throw new ArgumentNotValidException("Not found url: " + oldValue
                    + " of ad with id " + id);
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
    public void delete(String type, Long id, URI url) {
        switch (getTypeImage(type)) {
            case AD -> deleteFromAd(id, url);
            case AVATAR -> deleteFromUser(id, url);
            default -> throw new ArgumentNotValidException("Not found url: " + url
                    + " of ad with id " + id);
        }
    }

    private void deleteFromUser(Long id, URI url) {
        var user = getUser(id);
        deleteFile(url);
        user.setAvatar(BLANK_VALUE);
        userService.save(user);
    }

    private void deleteFromAd(Long id, URI url) {
        var ad = adService.getAd(id);
        var images = ad.getImages();
        if (!images.removeIf(value -> value.equals(url.toString()))) {
            throw new ArgumentNotValidException("Not found url: " + url
                    + " of ad with id " + id);
        }
        deleteFile(url);
    }

    @Override
    public void deleteFile(URI oldValue) {
        String fileName = Path.of(oldValue.getPath()).getFileName().toString();
        var lastDotIndex = fileName.indexOf(SEPARATOR_NAME_AND_EXTENSION);
        var key = fileName.substring(0, lastDotIndex);
        fileUpload.deleteFile(key);
    }

    private static TypeImage getTypeImage(String type) {
        try {
            return TypeImage.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ArgumentNotValidException("Image type is ad or avatar");
        }
    }

    private User getUser(Long id) {
        var currentUser = userService.getCurrentUser();
        return currentUser.getRole().equals(Role.ROLE_ROOT)
                || currentUser.getRole().equals(Role.ROLE_ADMIN)
                ? userService.getUserById(id) : currentUser;
    }

    private String getAndSaveToAd(MultipartFile file, Set<String> images, Ad ad) {
        var url = fileUpload.uploadFile(file, UUID.randomUUID() + SEPARATOR_NAME_AND_IMAGE
                + file.getOriginalFilename());
        images.add(url);
        ad.setImages(images);
        adService.save(ad);
        return url;
    }

    private String getAndSaveToUser(MultipartFile file, User user) {
        var url = fileUpload.uploadFile(file, UUID.randomUUID() + SEPARATOR_NAME_AND_IMAGE
                + file.getOriginalFilename());
        user.setAvatar(url);
        userService.save(user);
        return url;
    }
}
