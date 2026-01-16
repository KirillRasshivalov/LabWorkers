package algo.demo.services;

import algo.demo.database.CoordinatesTable;
import algo.demo.database.LabWorkTable;
import algo.demo.dto.StatisticInfo;
import algo.demo.repository.CoordinatesRepository;
import algo.demo.repository.LabWorkRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.api.sync.RedisCommands;
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

    @Inject
    private RedisCommands<String, String> redis;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private <T> T getFromCache(String key, Class<T> type) {
        try {
            String json = redis.get(key);
            if (json != null) {
                return objectMapper.readValue(json, type);
            }
        } catch (Exception e) {
            logger.warn("Ошибка чтения из кэша по ключу {}: {}", key, e.getMessage());
        }
        return null;
    }

    private <T> T getFromCacheList(String key, TypeReference<T> typeRef) {
        try {
            String json = redis.get(key);
            if (json != null) {
                return objectMapper.readValue(json, typeRef);
            }
        } catch (Exception e) {
            logger.warn("Ошибка чтения списка из кэша по ключу {}: {}", key, e.getMessage());
        }
        return null;
    }

    private void putInCache(String key, Object value) {
        try {
            String json = objectMapper.writeValueAsString(value);
            redis.setex(key, 300, json);
        } catch (Exception e) {
            logger.warn("Ошибка записи в кэш по ключу {}: {}", key, e.getMessage());
        }
    }

    public Double middleValueOfMinPoint() {
        String key = "stats:middleValue";
        Double cached = getFromCache(key, Double.class);
        if (cached != null) return cached;
        try {
            Double result = labWorkRepository.getMiddleValue();
            if (result != null) {
                putInCache(key, result);
            }
            return result;
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
        String key = "stats:uniqueMass";
        List<Integer> cached = getFromCacheList(key, new TypeReference<List<Integer>>() {});
        if (cached != null) return cached;
        try {
            List<Integer> result = labWorkRepository.getUnicMinimalPoints();
            if (result != null) {
                putInCache(key, result);
            }
            return result;
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

    public StatisticInfo collectStatistics(String username) {
        String key = "stats:global";
        StatisticInfo cached = getFromCache(key, StatisticInfo.class);
        if (cached != null) return cached;
        try {
            StatisticInfo result = labWorkRepository.getStatisticInfo();
            if (result != null) {
                putInCache(key, result);
            }
            return result;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    public void evictStatisticsCache() {
        try {
            redis.del("stats:middleValue", "stats:uniqueMass", "stats:global");
            logger.info("Кэш статистики успешно сброшен");
        } catch (Exception e) {
            logger.warn("Не удалось сбросить кэш статистики", e);
        }
    }
}

