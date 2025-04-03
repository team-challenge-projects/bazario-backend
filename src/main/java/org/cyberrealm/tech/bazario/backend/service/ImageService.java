package org.cyberrealm.tech.bazario.backend.service;

import java.net.URI;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    String save(String type, Long id, MultipartFile file);

    String change(String type, Long id, URI oldValue, MultipartFile file);

    void delete(String type, Long id, URI url);

    void deleteFile(URI oldValue);
}
