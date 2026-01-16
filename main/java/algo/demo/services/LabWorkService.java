package algo.demo.services;

import algo.demo.database.*;
import algo.demo.dto.LabWork;
import algo.demo.exceptions.DefaultException;
import algo.demo.repository.LabWorkRepository;
import algo.demo.validators.CollectionValidator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ApplicationScoped
public class LabWorkService {

    private static final Logger logger = LoggerFactory.getLogger(LabWorkService.class.getName());

    @Inject
    private LabWorkRepository labWorkRepository;

    @Inject
    private CollectionValidator collectionValidator;

    @Inject
    private SpecialOperationsService specialOperationsService;

    public LabWorkTable addLabWork(LabWork labWork) {
        try {
            collectionValidator.validateLabWork(labWork);
            specialOperationsService.evictStatisticsCache();
            return labWorkRepository.addLbWork(labWork);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new DefaultException(e.getMessage());
        }
    }

    public LabWorkTable updateLabWork(Long id, LabWork labWork) {
        try {
            collectionValidator.validateLabWork(labWork);
            specialOperationsService.evictStatisticsCache();
            return labWorkRepository.updateLabWorkById(id, labWork);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new DefaultException(e.getMessage());
        }
    }

    public void deleteLabWork(Long id) {
        try {
            labWorkRepository.deleteLabWorkById(id);
            specialOperationsService.evictStatisticsCache();
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new DefaultException(e.getMessage());
        }
    }

    public List<LabWorkTable> getAllLabWorks() {
        try {
            return labWorkRepository.getAllLabWorkTables();
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new DefaultException(e.getMessage());
        }
    }

    public LabWorkTable getLabWorkById(Long id) {
        try {
            return labWorkRepository.getLabWorkById(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new DefaultException(e.getMessage());
        }
    }
}
