package com.phanta.waladom.fileUpload;

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

    @Value("${aws.bucket.name}")
    private String BUCKET_NAME;

    @Autowired
    private final S3Client s3Client;

    public FileUploadService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String storeFile(MultipartFile file, String folder, String prefix, String uniqueId) {
        try {
            String extension = getFileExtension(file.getOriginalFilename());
            String filename = folder + prefix + "-" + uniqueId + extension;

            // Determine ACL based on folder type
            String aclPermission = determineAclPermission(folder);

            // Upload to S3
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(filename)  // The folder structure is included here
                    .contentType(file.getContentType())
                    .acl(aclPermission)
                    .build();

            PutObjectResponse response = s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            if (response.sdkHttpResponse().isSuccessful()) {
                return "https://" + BUCKET_NAME + ".s3.amazonaws.com/" + filename;
            } else {
                throw new RuntimeException("File upload failed with status: " + response.sdkHttpResponse().statusCode());
            }

        } catch (IOException e) {
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
        return filename.substring(filename.lastIndexOf("."));
    }


//
//    public String saveToSpecificFolder(MultipartFile file, String targetFolder, String prefix, String uniqueId) {
//        try {
//            String extension = getFileExtension(file.getOriginalFilename());
//            String filename = prefix + "-" + uniqueId + extension;
//
//            Path dirPath = Paths.get(BASE_DIR + targetFolder);
//            Files.createDirectories(dirPath);
//
//            Path filePath = dirPath.resolve(filename);
//            file.transferTo(filePath.toFile());
//
//            return filePath.toString();
//        } catch (IOException e) {
//            throw new RuntimeException("File upload failed", e);
//        }
//    }





}
