package algo.demo.services;

import io.minio.*;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

@ApplicationScoped
public class MinioService {

    private static final Logger logger = LoggerFactory.getLogger(MinioService.class);

    private MinioClient minioClient;
    private final String bucketName = "import-files";

    @PostConstruct
    public void init() {
        try {
            String endpoint = System.getenv("MINIO_ENDPOINT") != null ?
                    System.getenv("MINIO_ENDPOINT") : "http://localhost:9000";
            String accessKey = System.getenv("MINIO_ACCESS_KEY") != null ?
                    System.getenv("MINIO_ACCESS_KEY") : "minioadmin";
            String secretKey = System.getenv("MINIO_SECRET_KEY") != null ?
                    System.getenv("MINIO_SECRET_KEY") : "minioadmin";
            minioClient = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
                logger.info("Bucket {} успешно создан", bucketName);
            }

        } catch (Exception e) {
            logger.error("Ошибка при инициализации MinIO клиента: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось инициализировать MinIO клиент", e);
        }
    }

    public String uploadFile(String fileName, InputStream inputStream, long size, String contentType)
            throws Exception {
        try {
            String objectName = System.currentTimeMillis() + "_" + fileName;
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(inputStream, size, -1)
                    .contentType(contentType)
                    .build());
            logger.info("Файл {} успешно загружен в MinIO как {}", fileName, objectName);
            return objectName;
        } catch (Exception e) {
            logger.error("Ошибка при загрузке файла в MinIO: {}", e.getMessage(), e);
            throw e;
        }
    }

    public InputStream downloadFile(String objectName) throws Exception {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            logger.error("Ошибка при скачивании файла из MinIO: {}", e.getMessage(), e);
            throw e;
        }
    }

    public void deleteFile(String objectName) throws Exception {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
            logger.info("Файл {} удален из MinIO", objectName);
        } catch (Exception e) {
            logger.error("Ошибка при удалении файла из MinIO: {}", e.getMessage(), e);
            throw e;
        }
    }
}
