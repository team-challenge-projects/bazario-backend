package org.cyberrealm.tech.bazario.backend.service.impl;

import com.cloudinary.Cloudinary;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.cyberrealm.tech.bazario.backend.exception.custom.ArgumentNotValidException;
import org.cyberrealm.tech.bazario.backend.exception.custom.NotFoundResourceException;
import org.cyberrealm.tech.bazario.backend.service.FileUpload;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileUploadImpl implements FileUpload {
    private final Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile multipartFile, String key) {
        try {
            return cloudinary
                    .uploader()
                    .upload(multipartFile.getBytes(), Map.of("public_id", key))
                    .get("url")
                    .toString().replace("http", "https");
        } catch (IOException e) {
            throw new ArgumentNotValidException("The file is damaged or missing.");
        }
    }

    @Override
    public void deleteFile(String key) {
        try {
            cloudinary.uploader().destroy(key, Map.of("invalidate", true));
        } catch (IOException e) {
            throw new NotFoundResourceException("Not delete file with id " + key);
        }

    }
}
