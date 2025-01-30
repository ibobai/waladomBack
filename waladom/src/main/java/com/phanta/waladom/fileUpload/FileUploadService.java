package com.phanta.waladom.fileUpload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;

@Service
class FileUploadService {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadService.class);

    @Value("${aws.bucket.name}")
    private String BUCKET_NAME;

    @Autowired
    private final S3Client s3Client;

    public FileUploadService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String storeFile(MultipartFile file, String folder, String prefix, String uniqueId) {
        logger.info("Starting file upload for file: {} in folder: {}", file.getOriginalFilename(), folder);
        try {
            String extension = getFileExtension(file.getOriginalFilename());
            String filename = folder + prefix + "-" + uniqueId + extension;

            // Determine ACL based on folder type
            String aclPermission = determineAclPermission(folder);
            logger.info("Determined ACL permission: {} for folder: {}", aclPermission, folder);

            // Upload to S3
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(filename)
                    .contentType(file.getContentType())
                    .acl(aclPermission)
                    .build();

            PutObjectResponse response = s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            if (response.sdkHttpResponse().isSuccessful()) {
                String fileUrl = "https://" + BUCKET_NAME + ".s3.amazonaws.com/" + filename;
                logger.info("File uploaded successfully: {}", fileUrl);
                return fileUrl;
            } else {
                logger.error("File upload failed with status: {}", response.sdkHttpResponse().statusCode());
                throw new RuntimeException("File upload failed with status: " + response.sdkHttpResponse().statusCode());
            }

        } catch (IOException e) {
            logger.error("File upload failed due to IOException: {}", e.getMessage());
            throw new RuntimeException("File upload failed", e);
        }
    }

    // Function to determine ACL based on folder
    private String determineAclPermission(String folder) {
        if (folder.startsWith("media/report/") ||
                folder.startsWith("media/event/") ||
                folder.startsWith("media/waladom/logo/")) {
            return "public-read";  // Public access for these folders
        } else {
            return "private";  // Private for user-sensitive folders
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            logger.warn("Filename does not contain an extension: {}", filename);
            return ""; // Return empty extension if not found
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}
