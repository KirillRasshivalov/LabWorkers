package algo.demo.services;

import algo.demo.dto.FileHistory;
import algo.demo.dto.LabWork;
import algo.demo.parsers.FileParser;
import algo.demo.repository.FileHistoryRepository;
import algo.demo.repository.LabWorkRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;

@ApplicationScoped
public class FileDumpService {

    private static final Logger logger = LoggerFactory.getLogger(FileDumpService.class.getName());

    @Inject
    private FileParser fileParser;

    @Inject
    private FileHistoryRepository fileHistoryRepository;

    @Inject
    private LabWorkRepository labWorkRepository;

    @Inject
    private TransactionCoordinatorService transactionCoordinator;

    public void parseFile(InputStream fileInputStream, String nameOfUser, String fileName) {
        logger.info("Парсинг входного файла: {} от пользователя: {}", fileName, nameOfUser);

        InputStream fileCopyForParsing = null;
        InputStream fileCopyForStorage = null;
        try {
            byte[] fileBytes = fileInputStream.readAllBytes();
            fileCopyForParsing = new java.io.ByteArrayInputStream(fileBytes);
            fileCopyForStorage = new java.io.ByteArrayInputStream(fileBytes);
            String contentType = determineContentType(fileName);
            InputStream finalFileCopyForParsing = fileCopyForParsing;
            transactionCoordinator.saveFileWithTransaction(
                    fileCopyForStorage,
                    fileName,
                    nameOfUser,
                    contentType,
                    () -> {
                        List<LabWork> labWorkers = fileParser.parseLabWorksFromFile(finalFileCopyForParsing);
                        for (LabWork labWork : labWorkers) {
                            labWorkRepository.addLbWork(labWork);
                        }
                        logger.info("Успешно добавлено {} лаб работ в БД", labWorkers.size());
                    }
            );
        } catch (Exception e) {
            logger.error("Ошибка при парсинге файла {}: {}", fileName, e.getMessage(), e);
            FileHistory fileHistory = FileHistory.builder()
                    .status("error")
                    .user(nameOfUser)
                    .numOfAdds(0L)
                    .fileName(fileName)
                    .fileObjectName(null)
                    .build();
            fileHistoryRepository.save(fileHistory);
            throw new RuntimeException("Ошибка при обработке файла: " + e.getMessage(), e);
        } finally {
            closeQuietly(fileCopyForParsing);
            closeQuietly(fileCopyForStorage);
        }
    }

    public void parseFile(InputStream fileInputStream, String nameOfUser) {
        parseFile(fileInputStream, nameOfUser, "uploaded_file.txt");
    }

    private String determineContentType(String fileName) {
        if (fileName == null) {
            return "application/octet-stream";
        }
        fileName = fileName.toLowerCase();
        if (fileName.endsWith(".txt") || fileName.endsWith(".text")) {
            return "text/plain";
        } else if (fileName.endsWith(".csv")) {
            return "text/csv";
        } else if (fileName.endsWith(".json")) {
            return "application/json";
        } else if (fileName.endsWith(".xml")) {
            return "application/xml";
        } else if (fileName.endsWith(".pdf")) {
            return "application/pdf";
        } else if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
            return "application/vnd.ms-excel";
        } else {
            return "application/octet-stream";
        }
    }

    private void closeQuietly(InputStream stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (Exception e) {
                logger.warn("Не удалось закрыть поток: {}", e.getMessage());
            }
        }
    }
}