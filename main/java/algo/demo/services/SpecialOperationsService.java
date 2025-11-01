package algo.demo.services;

import algo.demo.database.CoordinatesTable;
import algo.demo.database.LabWorkTable;
import algo.demo.repository.CoordinatesRepository;
import algo.demo.repository.LabWorkRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ApplicationScoped
public class SpecialOperationsService {

    private static final Logger logger = LoggerFactory.getLogger(SpecialOperationsService.class.getName());

    @Inject
    private LabWorkRepository labWorkRepository;

    @Inject
    private CoordinatesRepository coordinatesRepository;

    public Double middleValueOfMinPoint() {
        try {
            return labWorkRepository.getMiddleValue();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return 0.0;
        }
    }

    public CoordinatesTable getMinCoordinates() {
        try {
            return coordinatesRepository.minValueOfCoordinates();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public LabWorkTable getLabWorkByMinId() {
        try {
            return labWorkRepository.getLabWorkByMinCoordinate();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public List<Integer> getUnicMass() {
        try {
            return labWorkRepository.getUnicMinimalPoints();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public void decreaseDifficultInLabWork(Long id, Integer num) {
        try {
            labWorkRepository.decreaseDifficultyInLabWork(id, num);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}

