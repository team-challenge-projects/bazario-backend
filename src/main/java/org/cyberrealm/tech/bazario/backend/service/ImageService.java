package org.cyberrealm.tech.bazario.backend.service;

import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

public interface ImageService {
    String addToAd(Long id, MultipartFile file);

    String save(String type, Long id, MultipartFile file);

    String change(String type, Long id, URI oldValue, MultipartFile file);
}
