package algo.demo.services;

import algo.demo.dto.FileHistory;
import algo.demo.repository.FileHistoryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.UUID;

@ApplicationScoped
public class TransactionCoordinatorService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionCoordinatorService.class);

    @Inject
    private MinioService minioService;

    @Inject
    private FileHistoryRepository fileHistoryRepository;

    public void saveFileWithTransaction(InputStream fileInputStream, String fileName,
                                        String username, String contentType,
                                        Runnable databaseOperation) throws Exception {

        String tempObjectName = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            byte[] fileBytes = baos.toByteArray();
            ByteArrayInputStream fileCopy1 = new ByteArrayInputStream(fileBytes);
            ByteArrayInputStream fileCopy2 = new ByteArrayInputStream(fileBytes);
            tempObjectName = "temp_" + UUID.randomUUID() + "_" + fileName;
            String finalObjectName = minioService.uploadFile(tempObjectName, fileCopy1, fileBytes.length, contentType);
            logger.info("Фаза 1 (Prepare): Файл временно сохранен в MinIO как {}", tempObjectName);
            try {
                databaseOperation.run();
                String permanentObjectName = System.currentTimeMillis() + "_" + fileName;
                String permanentName = minioService.uploadFile(permanentObjectName, fileCopy2, fileBytes.length, contentType);
                minioService.deleteFile(tempObjectName);
                FileHistory fileHistory = FileHistory.builder()
                        .status("ok")
                        .user(username)
                        .numOfAdds(0L)
                        .fileName(fileName)
                        .fileObjectName(permanentName)
                        .build();

                fileHistoryRepository.save(fileHistory);
                logger.info("Фаза 2 (Commit): Транзакция успешно завершена");
            } catch (Exception dbException) {
                logger.error("Ошибка при операции с БД, откатываем файл из MinIO");
                minioService.deleteFile(tempObjectName);
                throw dbException;
            }
        } catch (Exception e) {
            logger.error("Ошибка в распределенной транзакции: {}", e.getMessage(), e);
            if (tempObjectName != null) {
                try {
                    minioService.deleteFile(tempObjectName);
                } catch (Exception deleteEx) {
                    logger.error("Не удалось удалить временный файл: {}", deleteEx.getMessage());
                }
            }
            throw e;
        }
    }
}