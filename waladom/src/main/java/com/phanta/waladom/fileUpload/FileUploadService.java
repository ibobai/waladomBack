package com.phanta.waladom.fileUpload;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Service
class FileUploadService {

    private final String BASE_DIR = System.getProperty("user.dir") + "/var/www/media/";  // Absolute path

    public String storeFile(MultipartFile file, String folder, String prefix, String uniqueId) {
        try {
            String extension = getFileExtension(file.getOriginalFilename());
            String filename = prefix + "-" + uniqueId + extension;

            Path dirPath = Paths.get(BASE_DIR + folder + uniqueId);
            Files.createDirectories(dirPath);

            Path filePath = dirPath.resolve(filename);
            file.transferTo(filePath.toFile());

            return filePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }
    }

    public String saveToSpecificFolder(MultipartFile file, String targetFolder, String prefix, String uniqueId) {
        try {
            String extension = getFileExtension(file.getOriginalFilename());
            String filename = prefix + "-" + uniqueId + extension;

            Path dirPath = Paths.get(BASE_DIR + targetFolder);
            Files.createDirectories(dirPath);

            Path filePath = dirPath.resolve(filename);
            file.transferTo(filePath.toFile());

            return filePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }
    }



    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }


}
