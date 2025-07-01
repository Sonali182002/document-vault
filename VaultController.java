package com.example.vault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;

@RestController
@RequestMapping("/vault")
public class VaultController {

    private final FileEncryptionService encryptionService;

    public VaultController() throws Exception {
        // In real apps, load key from config or secure vault
        byte[] keyBytes = FileEncryptionService.generateKey();
        encryptionService = new FileEncryptionService(keyBytes);
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) throws Exception {
        File encryptedFile = new File("vault/" + file.getOriginalFilename() + ".enc");
        try (InputStream in = file.getInputStream();
             OutputStream out = new FileOutputStream(encryptedFile)) {
            encryptionService.encryptFile(in, out);
        }
        return "File uploaded and encrypted!";
    }

    @GetMapping("/download/{filename}")
    public void download(@PathVariable String filename, HttpServletResponse response) throws Exception {
        File encryptedFile = new File("vault/" + filename + ".enc");
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);
        try (InputStream in = new FileInputStream(encryptedFile);
             OutputStream out = response.getOutputStream()) {
            encryptionService.decryptFile(in, out);
        }
    }
}
