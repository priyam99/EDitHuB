package com.edithub.media.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class S3StorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${app.storage.bucket-media:edithub-media}")
    private String mediaBucket;

    @Value("${app.storage.presigned-url-expiration-minutes:60}")
    private long expirationMinutes;

    public String generatePresignedUploadUrl(String storageKey, String contentType, long fileSize) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(mediaBucket)
                .key(storageKey)
                .contentType(contentType)
                .contentLength(fileSize)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(expirationMinutes))
                .putObjectRequest(objectRequest)
                .build();

        return s3Presigner.presignPutObject(presignRequest).url().toString();
    }

    public String generatePresignedDownloadUrl(String storageKey) {
        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(mediaBucket)
                .key(storageKey)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(expirationMinutes))
                .getObjectRequest(objectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    public boolean checkObjectExists(String storageKey) {
        try {
            HeadObjectRequest headRequest = HeadObjectRequest.builder()
                    .bucket(mediaBucket)
                    .key(storageKey)
                    .build();
            s3Client.headObject(headRequest);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getMediaBucket() {
        return mediaBucket;
    }
}
