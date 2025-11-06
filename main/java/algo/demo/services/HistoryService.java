package algo.demo.services;

import algo.demo.database.ImportHistoryTable;
import algo.demo.enums.Roles;
import algo.demo.exceptions.HistoryException;
import algo.demo.repository.FileHistoryRepository;
import algo.demo.repository.UserRepository;
import algo.demo.security.JwtUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ApplicationScoped
public class HistoryService {

    private static final Logger logger = LoggerFactory.getLogger(HistoryService.class.getName());

    @Inject
    private JwtUtil jwtUtil;

    @Inject
    private FileHistoryRepository fileHistoryRepository;

    @Inject
    private UserRepository userRepository;

    public List<ImportHistoryTable> getHistoryOfImports(String jwt) {
        try {
            if (userRepository.getRole(jwtUtil.extractUsername(jwt)).equals(Roles.ADMIN)) {
                logger.info("Успешно удалось получить все данные об импортах");
                return fileHistoryRepository.findAll();
            } else {
                logger.info("Успешно удалось получить все данные об импортах");
                return fileHistoryRepository.findCurrantUserHistory(jwtUtil.extractUsername(jwt));
            }
        } catch (Exception e) {
            logger.error("Произошла ошибка при выгрузке импортов файлов.");
            throw new HistoryException(e.getMessage());
        }
    }
}
