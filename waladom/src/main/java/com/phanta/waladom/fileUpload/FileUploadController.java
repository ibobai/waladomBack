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

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping("/files")
    public ResponseEntity<Map<String, Object>> uploadFiles(@RequestParam Map<String, MultipartFile> files) {
        System.out.println("Received files: " + files.size());
        Map<String, Object> response = new HashMap<>();

        try {

            for (Map.Entry<String, MultipartFile> entry : files.entrySet()) {
                String key = entry.getKey();
                MultipartFile file = entry.getValue();
                String folder = determineFolder(key);
                String savedFilePath = fileUploadService.storeFile(file, folder, key);
                response.put(key, savedFilePath);
            }
        }catch (Exception ex){
            response.put("Exception 500 ", ex.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    private String determineFolder(String key) {
        Map<String, String> folderMapping = Map.of(
                "idProof", "user/idProofs/",
                "profile", "user/profilePhoto/",
                "reqIdProof", "registration/reqIdProofs/",
                "reqProfile", "registration/reqProfilePhoto/",
                "reportProof", "report/proof/",
                "event", "event/",
                "waladom", "waladom/logo/",
                "file", "waladom/others/"
        );
        return folderMapping.getOrDefault(key, "waladom/others/");
    }
}