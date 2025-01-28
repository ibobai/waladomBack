package com.phanta.waladom.fileUpload;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.time.Duration;

@Service
public class S3Service {

    private final S3Client s3Client;

    private final S3Presigner s3Presigner;

    @Value("${aws.bucket.name}")
    private String bucketName;

    @Value("${aws.base.dir}")
    private String baseDir;

    public S3Service(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    public String generatePresignedUrl(String objectKey) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(extractAfterStartWord(baseDir,objectKey))
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))  // URL valid for 15 minutes
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    // Method to extract the part after the startWord in the given url
    public static String extractAfterStartWord(String startWord, String url) {
        // Add the slash after the start word for consistency
        String startWithSlash = startWord + "/";

        // Find the index of the start word followed by a slash
        int startIndex = url.indexOf(startWithSlash);

        // If the start word is found, extract the part after it
        if (startIndex != -1) {
            return url.substring(startIndex);
        } else {
            // If not found, return an empty string or handle it as needed
            return "";
        }
    }

    // Method to delete a photo or folder from S3 and return a boolean status
    public boolean deletePhotoOrFolderFromS3(String url) {
        // Extract the S3 object key from the URL (assuming it follows the same structure)
        String key = extractKeyFromUrl(url);

        if (key != null) {
            try {
                // Check if the key ends with a "/". This would indicate a "folder" (prefix).
                if (key.endsWith("/")) {
                    // Delete all objects in the "folder"
                    deleteObjectsInFolder(key);
                } else {
                    // Delete the specific object (photo)
                    s3Client.deleteObject(DeleteObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build());
                }
                return true;  // Return true if delete is successful
            } catch (Exception e) {
                return false;  // Return false if an error occurs
            }
        } else {
            return false;  // Return false if key extraction fails
        }
    }


    // Method to delete all objects in a folder (prefix) in S3
    private void deleteObjectsInFolder(String folderPrefix) {
        // List all objects in the folder
        ListObjectsV2Request listObjects = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(folderPrefix)  // Folder prefix
                .build();

        ListObjectsV2Response response = s3Client.listObjectsV2(listObjects);

        // Delete each object in the folder
        for (S3Object s3Object : response.contents()) {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Object.key())
                    .build());
        }

    }
    // Helper method to extract the key from the S3 URL
    private String extractKeyFromUrl(String url) {
        String baseUrl = "https://"+ bucketName + ".s3.amazonaws.com/";  // Replace with your actual base URL
        if (url.startsWith(baseUrl)) {
            return url.substring(baseUrl.length());  // Extract the key part of the URL
        }
        return null;
    }
}
