package com.jaster25.communitybackend.global.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.jaster25.communitybackend.global.exception.ErrorCode;
import com.jaster25.communitybackend.global.exception.custom.FileUploadException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AmazonS3Util {

    private final AmazonS3Client amazonS3Client;

    @Value("${S3_BUCKET}")
    private String bucket;

    public String upload(MultipartFile multipartFile) {
        String newFileName = generateFileName(multipartFile);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());

        try {
            amazonS3Client.putObject(new PutObjectRequest(bucket, newFileName, multipartFile.getInputStream(), objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            return amazonS3Client.getUrl(bucket, newFileName).toString();
        } catch (IOException e) {
            throw new FileUploadException(ErrorCode.FILE_UPLOAD_FAILURE);
        }
    }

    public static String generateFileName(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            throw new FileUploadException(ErrorCode.EMPTY_FILE);
        }

        String originalFileName = multipartFile.getOriginalFilename();
        if (originalFileName == null || !originalFileName.contains(".")) {
            throw new FileUploadException(ErrorCode.INVALID_FILE_NAME);
        }

        int fileExtensionIndex = originalFileName.lastIndexOf('.');
        String fileExtension = originalFileName.substring(fileExtensionIndex);
        String fileName = originalFileName.substring(0, fileExtensionIndex);
        String now = String.valueOf(System.currentTimeMillis());
        return fileName + "_" + now + fileExtension;
    }
}
