package org.cyberrealm.tech.bazario.backend.service;

import java.net.URI;
import org.cyberrealm.tech.bazario.backend.dto.TypeImage;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    /**
     * Add image in ad, user avatar, category
     *
     * @author Andrey Sitarskiy
     * @param type Essentials of inserting the file
     * @param id Depending on the type: ad id, user id, category id
     * @param file Loading file
     * @return URL image
     */
    String save(TypeImage type, Long id, MultipartFile file);

    /**
     * Change image in ad, user avatar, category
     *
     * @author Andrey Sitarskiy
     * @param type Essentials of inserting the file
     * @param id Depending on the type: ad id, user id, category id
     * @param oldValue URL old file
     * @param file Loading file
     * @return URL image
     */
    String change(TypeImage type, Long id, URI oldValue, MultipartFile file);

    /**
     * Delete image in ad, user avatar, category
     *
     * @author Andrey Sitarskiy
     * @param type essentials of inserting the file
     * @param id Depending on the type: ad id, user id, category id
     */
    void delete(TypeImage type, Long id, URI url);

    /**
     * Delete image by url
     *
     * @author Andrey Sitarskiy
     * @param oldValue URL old file
     */
    void deleteFile(URI oldValue);
}
