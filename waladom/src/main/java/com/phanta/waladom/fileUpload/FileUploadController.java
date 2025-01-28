package com.phanta.waladom.fileUpload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    @Value("${aws.base.dir}")
    private String basedir;

    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping("/files")
    public ResponseEntity<Map<String, Object>> uploadFiles(@RequestParam Map<String, MultipartFile> files) {
        System.out.println("Received files: " + files.size());
        Map<String, Object> response = new HashMap<>();
        String uniqueId = generateUniqueId();

        try {
            for (Map.Entry<String, MultipartFile> entry : files.entrySet()) {
                String key = entry.getKey();
                MultipartFile file = entry.getValue();
                String folder = basedir + "/" + determineFolder(key)+uniqueId+"/";
                String savedFilePath = fileUploadService.storeFile(file, folder, key, uniqueId);
                response.put(key, savedFilePath);
            }
        } catch (Exception ex) {
            response.put("error", ex.getMessage());
            response.put("errorCode", 500);
            return ResponseEntity.internalServerError().body(response);
        }
        return ResponseEntity.ok(response);
    }


    @Autowired
    private S3Service s3Service;

    // Get signed URLs for multiple photos of a single user
    @PostMapping("/signephoto")
    public Map<String, String> getSignedUrls(@RequestBody List<String> fileKeys) {
        Map<String, String> signedUrls = new HashMap<>();

        for (String fileKey : fileKeys) {
            signedUrls.put(fileKey, s3Service.generatePresignedUrl(fileKey));
        }

        return signedUrls;  // Return key-value pairs (file key -> signed URL)
    }

    // Get signed URLs for multiple users and their respective photo lists
    @PostMapping("/signephotos")
    public Map<String, Map<String, String>> getBatchSignedUrls(@RequestBody Map<String, List<String>> userPhotos) {
        Map<String, Map<String, String>> result = new HashMap<>();

        for (Map.Entry<String, List<String>> entry : userPhotos.entrySet()) {
            String userId = entry.getKey();
            List<String> fileKeys = entry.getValue();

            Map<String, String> signedUrls = new HashMap<>();
            for (String fileKey : fileKeys) {
                signedUrls.put(fileKey, s3Service.generatePresignedUrl(fileKey));
            }

            result.put(userId, signedUrls);
        }

        return result;  // Returns { userId: { fileKey1: signedUrl1, fileKey2: signedUrl2 } }
    }

    private String determineFolder(String key) {
        Map<String, String> folderMapping = Map.of(
                "idProofFront", "user/idProofs/",
                "idProofBack", "user/idProofs/",
                "profile", "user/profilePhotos/",
                "reqIdProofFront", "registration/reqIdProofs/",
                "reqIdProofBack", "registration/reqIdProofs/",
                "reqProfile", "registration/reqProfilePhotos/",
                "waladom", "waladom/logo/"
        );

        // Check for exact matches first
        for (Map.Entry<String, String> entry : folderMapping.entrySet()) {
            if (key.equals(entry.getKey())) {
                return entry.getValue();
            }
        }

        // Check for keys that start with specific prefixes
        if (key.startsWith("event")) {
            return "event/";
        }
        if (key.startsWith("report")) {
            return "report/proofs/";
        }
        if (key.startsWith("file")) {
            return "waladom/others/";
        }

        // Default fallback folder
        return "waladom/others/";
    }


    private String generateUniqueId() {
        return UUID.randomUUID().toString().substring(0, 7) + getRandomLetters();
    }

    private String getRandomLetters() {
        char letter1 = (char) ('A' + Math.random() * 26);
        char letter2 = (char) ('A' + Math.random() * 26);
        return "" + letter1 + letter2;
    }


}