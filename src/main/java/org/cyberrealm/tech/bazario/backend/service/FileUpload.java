package org.cyberrealm.tech.bazario.backend.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileUpload {
    String uploadFile(MultipartFile multipartFile, String key);

    void deleteFile(String key);
}
