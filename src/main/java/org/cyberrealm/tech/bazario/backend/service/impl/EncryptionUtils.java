package org.cyberrealm.tech.bazario.backend.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EncryptionUtils {
    private static final String ALGORITHM = "AES";
    private final Cipher encryptionCipher;
    private final Cipher decryptionCipher;

    public EncryptionUtils(@Value("${aes.secret}") String secret) throws Exception {
        byte[] key = secret.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec keySpec = new SecretKeySpec(key, ALGORITHM);

        encryptionCipher = Cipher.getInstance(ALGORITHM);
        encryptionCipher.init(Cipher.ENCRYPT_MODE, keySpec);

        decryptionCipher = Cipher.getInstance(ALGORITHM);
        decryptionCipher.init(Cipher.DECRYPT_MODE, keySpec);
    }

    public String encrypt(String data) throws Exception {
        byte[] encryptedBytes = encryptionCipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public String decrypt(String encryptedData) throws Exception {
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] originalBytes = decryptionCipher.doFinal(decodedBytes);
        return new String(originalBytes, StandardCharsets.UTF_8);
    }
}
