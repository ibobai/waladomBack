package com.phanta.waladom.fileUpload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

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
                String folder = determineFolder(key);
                String savedFilePath = fileUploadService.storeFile(file, folder, key, uniqueId);
                response.put(key, savedFilePath);
            }
        }catch (Exception ex){
            response.put("Exception 500 ", ex.getMessage());
        }
        return ResponseEntity.ok(response);
    }
    private String determineFolder(String key) {
        Map<String, String> folderMapping = Map.of(
                "idProofFront", "user/idProofs/",
                "idProofBack", "user/idProofs/",
                "profile", "user/profilePhoto/",
                "reqIdProofFront", "registration/reqIdProofs/",
                "reqIdProofBack", "registration/reqIdProofs/",
                "reqProfile", "registration/reqProfilePhoto/",
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
        if (key.startsWith("reportProof")) {
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