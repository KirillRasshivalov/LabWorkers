package algo.demo.services;

import algo.demo.dto.FileHistory;
import algo.demo.dto.LabWork;
import algo.demo.exceptions.ParsingException;
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

    public void parseFile(InputStream fileInputStream, String nameOfUser) {
        logger.info("Парсинг входного файла.");
        try {
            List<LabWork> labWorkers = fileParser.parseLabWorksFromFile(fileInputStream);
            for (LabWork labWork : labWorkers) {
                labWorkRepository.addLbWork(labWork);
            }
            FileHistory fileHistory = new FileHistory("ok", nameOfUser, (long) labWorkers.size());
            fileHistoryRepository.save(fileHistory);
        } catch (Exception e) {
            logger.error(e.getMessage());
            FileHistory fileHistory = new FileHistory("error", nameOfUser, (long) 0);
            fileHistoryRepository.save(fileHistory);
            throw new RuntimeException(e.getMessage());
        }
    }
}
