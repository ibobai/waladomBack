package com.phanta.waladom.fileUpload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    @Value("${aws.base.dir}")
    private String basedir;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private S3Service s3Service;

    @PostMapping("/files")
    public ResponseEntity<Map<String, Object>> uploadFiles(@RequestParam Map<String, MultipartFile> files) {
        logger.info("Received request to upload {} files", files.size());
        Map<String, Object> response = new HashMap<>();
        String uniqueId = generateUniqueId();

        try {
            for (Map.Entry<String, MultipartFile> entry : files.entrySet()) {
                String key = entry.getKey();
                MultipartFile file = entry.getValue();
                String folder = basedir + "/" + determineFolder(key) + uniqueId + "/";
                logger.info("Processing file: {} - Folder: {}", key, folder);
                String savedFilePath = fileUploadService.storeFile(file, folder, key, uniqueId);
                response.put(key, savedFilePath);
                logger.info("File uploaded successfully: {} -> {}", key, savedFilePath);
            }
        } catch (Exception ex) {
            logger.error("Error uploading files: {}", ex.getMessage(), ex);
            response.put("error", ex.getMessage());
            response.put("errorCode", 500);
            return ResponseEntity.internalServerError().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signephoto")
    public Map<String, String> getSignedUrls(@RequestBody List<String> fileKeys) {
        logger.info("Generating signed URLs for {} files", fileKeys.size());
        Map<String, String> signedUrls = new HashMap<>();

        for (String fileKey : fileKeys) {
            signedUrls.put(fileKey, s3Service.generatePresignedUrl(fileKey));
            logger.info("Generated signed URL for file: {}", fileKey);
        }

        return signedUrls;
    }

    @PostMapping("/signephotos")
    public Map<String, Map<String, String>> getBatchSignedUrls(@RequestBody Map<String, List<String>> userPhotos) {
        logger.info("Generating batch signed URLs for {} users", userPhotos.size());
        Map<String, Map<String, String>> result = new HashMap<>();

        for (Map.Entry<String, List<String>> entry : userPhotos.entrySet()) {
            String userId = entry.getKey();
            List<String> fileKeys = entry.getValue();
            logger.info("Processing user: {} with {} files", userId, fileKeys.size());

            Map<String, String> signedUrls = new HashMap<>();
            for (String fileKey : fileKeys) {
                signedUrls.put(fileKey, s3Service.generatePresignedUrl(fileKey));
                logger.info("Generated signed URL for user: {}, file: {}", userId, fileKey);
            }
            result.put(userId, signedUrls);
        }
        return result;
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

        if (folderMapping.containsKey(key)) {
            return folderMapping.get(key);
        }

        if (key.startsWith("event")) return "event/";
        if (key.startsWith("report")) return "report/proofs/";
        if (key.startsWith("file")) return "waladom/others/";

        logger.warn("Unrecognized key: {}, using default folder", key);
        return "waladom/others/";
    }

    private String generateUniqueId() {
        String uniqueId = UUID.randomUUID().toString().substring(0, 7) + getRandomLetters();
        logger.debug("Generated unique ID: {}", uniqueId);
        return uniqueId;
    }

    private String getRandomLetters() {
        char letter1 = (char) ('A' + Math.random() * 26);
        char letter2 = (char) ('A' + Math.random() * 26);
        String randomLetters = "" + letter1 + letter2;
        logger.debug("Generated random letters: {}", randomLetters);
        return randomLetters;
    }
}
