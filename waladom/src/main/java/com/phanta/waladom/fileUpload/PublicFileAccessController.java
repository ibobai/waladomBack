package com.phanta.waladom.fileUpload;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/public")
public class PublicFileAccessController {

    private final String BASE_DIR = System.getProperty("user.dir") + "/var/www/media/";  // Absolute path
    //private final String BASE_DIR = "/home/ibo/projects/waladom/backend/waladomBackEnd/waladom/var/www/media/";
    @GetMapping("/{folder}/{*path}")
    public ResponseEntity<Resource> getFile(@PathVariable String folder, @PathVariable("path") String path) {
        try {
            if (!isAllowedFolder(folder)) {
                return ResponseEntity.badRequest().build();
            }

            Path filePath = Paths.get(BASE_DIR, folder, path).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = determineContentType(path);
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private boolean isAllowedFolder(String folder) {
        return folder.equals("event") || folder.equals("report"); // Add more as needed
    }

    private String determineContentType(String filename) {
        if (filename.endsWith(".png")) return "image/png";
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) return "image/jpeg";
        if (filename.endsWith(".pdf")) return "application/pdf";
        return "application/octet-stream";
    }
}
