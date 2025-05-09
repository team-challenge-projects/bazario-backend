package org.cyberrealm.tech.bazario.backend.service;

import java.net.URI;
import org.cyberrealm.tech.bazario.backend.dto.TypeImage;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    String save(TypeImage type, Long id, MultipartFile file);

    String change(TypeImage type, Long id, URI oldValue, MultipartFile file);

    void delete(TypeImage type, Long id, URI url);

    void deleteFile(URI oldValue);
}
