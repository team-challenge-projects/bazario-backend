package org.cyberrealm.tech.bazario.backend.api.impl;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.api.ImageApiDelegate;
import org.cyberrealm.tech.bazario.backend.dto.TypeImage;
import org.cyberrealm.tech.bazario.backend.service.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageApiDelegateImpl implements ImageApiDelegate {
    private final ImageService imageService;

    @Override
    public ResponseEntity<URI> changeImage(TypeImage type, Long id, URI oldValue,
                                              MultipartFile file) {
        var url = imageService.change(type, id, oldValue, file);
        return ResponseEntity.ok(URI.create(url));
    }

    @Override
    public ResponseEntity<Void> deleteImage(TypeImage type, Long id, URI url) {
        imageService.delete(type, id, url);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<URI> saveImage(TypeImage type, Long id, MultipartFile file) {
        var url = imageService.save(type, id, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(URI.create(url));
    }
}
