package org.cyberrealm.tech.bazario.backend.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileUpload {
    /**
     * Add or modify a file on a remote service
     *
     * @author Andrey Sitarskiy
     * @param multipartFile Loading file
     * @param key Unique Name image
     */
    String uploadFile(MultipartFile multipartFile, String key);

    /**
     * Delete a file on a remote service
     *
     * @author Andrey Sitarskiy
     * @param key Unique Name image
     */
    void deleteFile(String key);
}
