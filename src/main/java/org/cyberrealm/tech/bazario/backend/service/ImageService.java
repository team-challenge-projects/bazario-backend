package org.cyberrealm.tech.bazario.backend.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    String addImageInAd(Long id, MultipartFile file);
}
