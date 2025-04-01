package org.cyberrealm.tech.bazario.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.exception.custom.ForbiddenException;
import org.cyberrealm.tech.bazario.backend.service.AdService;
import org.cyberrealm.tech.bazario.backend.service.FileUpload;
import org.cyberrealm.tech.bazario.backend.service.ImageService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final AdService adService;
    private final FileUpload fileUpload;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public String addImageInAd(Long id, MultipartFile file) {
        var ad = adService.getAd(id);
        var images = ad.getImages();

        if (images.size() > 6) throw new ForbiddenException("Size of images is too large");

        var url = fileUpload.uploadFile(file, UUID.randomUUID() + "_"
                + file.getOriginalFilename());
        images.add(url);
        ad.setImages(images);
        adService.save(ad);
        return url;
    }
}
