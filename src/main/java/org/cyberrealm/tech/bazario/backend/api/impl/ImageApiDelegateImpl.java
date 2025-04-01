package org.cyberrealm.tech.bazario.backend.api.impl;

import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.api.ImageApiDelegate;
import org.cyberrealm.tech.bazario.backend.service.ImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class ImageApiDelegateImpl implements ImageApiDelegate {

    private final ImageService imageService;

    @Override
    public ResponseEntity<String> changeImage(String type, Long id, URI oldValue, MultipartFile file) {
        var url = imageService.change(type, id, oldValue, file);
        return ImageApiDelegate.super.changeImage(type, id, oldValue, file);
    }

    @Override
    public ResponseEntity<Void> deleteImage(String type, Long id, URI url) {
        return ImageApiDelegate.super.deleteImage(type, id, url);
    }

    @Override
    public ResponseEntity<String> saveImage(String type, Long id, MultipartFile file) {
        var url = imageService.save(type, id, file);
        return ResponseEntity.ok(url);
    }
}
