package com.example.vault;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.SecureRandom;

public class FileEncryptionService {
    private static final String ALGO = "AES";
    private final SecretKey secretKey;

    public FileEncryptionService(byte[] keyBytes) {
        this.secretKey = new SecretKeySpec(keyBytes, ALGO);
    }

    // Generate a new AES key (store securely!)
    public static byte[] generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGO);
        keyGen.init(128, new SecureRandom());
        return keyGen.generateKey().getEncoded();
    }

    public void encryptFile(InputStream in, OutputStream out) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        processFile(cipher, in, out);
    }

    public void decryptFile(InputStream in, OutputStream out) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        processFile(cipher, in, out);
    }

    private void processFile(Cipher cipher, InputStream in, OutputStream out) throws Exception {
        byte[] buffer = new byte[1024];
        int numRead;
        while ((numRead = in.read(buffer)) != -1) {
            byte[] output = cipher.update(buffer, 0, numRead);
            if (output != null)
                out.write(output);
        }
        byte[] outputBytes = cipher.doFinal();
        if (outputBytes != null)
            out.write(outputBytes);
    }
}