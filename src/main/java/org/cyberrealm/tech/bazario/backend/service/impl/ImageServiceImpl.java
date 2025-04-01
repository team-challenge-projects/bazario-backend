package org.cyberrealm.tech.bazario.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.exception.custom.ArgumentNotValidException;
import org.cyberrealm.tech.bazario.backend.exception.custom.ForbiddenException;
import org.cyberrealm.tech.bazario.backend.model.Ad;
import org.cyberrealm.tech.bazario.backend.model.enums.Role;
import org.cyberrealm.tech.bazario.backend.service.AdService;
import org.cyberrealm.tech.bazario.backend.service.FileUpload;
import org.cyberrealm.tech.bazario.backend.service.ImageService;
import org.cyberrealm.tech.bazario.backend.service.UserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    public static final String DELIMITER_NAME_IMAGE = "_";
    private int maxNumImages = 6;

    private final AdService adService;
    private final UserService userService;
    private final FileUpload fileUpload;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public String save(String type, Long id, MultipartFile file) {
        return switch (type) {
            case "ad" -> addToAd(id, file);
            case "avatar" -> addToUser(id, file);
            default -> throw new ArgumentNotValidException(
                    "Image type is ad or avatar");
        };
    }

    private String addToUser(Long id, MultipartFile file) {
        var currentUser = userService.getCurrentUser();
        var user = currentUser.getRole().equals(Role.ROLE_ROOT)
                || currentUser.getRole().equals(Role.ROLE_ADMIN)
                ? userService.getUserById(id) : currentUser;
        var url = fileUpload.uploadFile(file, UUID.randomUUID() + DELIMITER_NAME_IMAGE
                + file.getOriginalFilename());
        user.setAvatar(url);
        userService.save(user);
        return url;
    }

    @Override
    public String addToAd(Long id, MultipartFile file) {
        var ad = adService.getAd(id);
        var images = ad.getImages();

        if (images.size() > maxNumImages) throw new ForbiddenException("Size of images is too large");

        return getAndSaveToAd(file, images, ad);
    }

    private String getAndSaveToAd(MultipartFile file, Set<String> images, Ad ad) {
        var url = fileUpload.uploadFile(file, UUID.randomUUID() + DELIMITER_NAME_IMAGE
                + file.getOriginalFilename());
        images.add(url);
        ad.setImages(images);
        adService.save(ad);
        return url;
    }

    @Override
    public String change(String type, Long id, URI oldValue, MultipartFile file) {
        return switch (type) {
            case "ad" -> changeToAd(id, oldValue,file);
            case "avatar" -> changeToUser(id, oldValue, file);
            default -> throw new ArgumentNotValidException(
                    "Image type is ad or avatar");
        };
    }

    private String changeToUser(Long id, URI oldValue, MultipartFile file) {
        var currentUser = userService.getCurrentUser();
        var user = currentUser.getRole().equals(Role.ROLE_ROOT)
                || currentUser.getRole().equals(Role.ROLE_ADMIN)
                ? userService.getUserById(id) : currentUser;

        var url = fileUpload.uploadFile(file, UUID.randomUUID() + DELIMITER_NAME_IMAGE
                + file.getOriginalFilename());
        user.setAvatar(url);
        userService.save(user);
        return url;
    }

    private String changeToAd(Long id, URI oldValue, MultipartFile file) {
        var ad = adService.getAd(id);
        var images = ad.getImages();
        if (!images.removeIf(url -> url.equals(oldValue.toString()))) {
            throw new ArgumentNotValidException("Not found url: " + oldValue
                    + " of ad with id " + id);
        }
        String fileName = Path.of(oldValue.getPath()).getFileName().toString();
        var lastDotIndex = fileName.indexOf(".");
        var key = fileName.substring(0, lastDotIndex);
        fileUpload.deleteFile(key);
        return getAndSaveToAd(file, images, ad);
    }
}
