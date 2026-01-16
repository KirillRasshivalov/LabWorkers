package algo.demo.controllers;

import algo.demo.EnvLoader;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/cache")
@Produces(MediaType.APPLICATION_JSON)
public class CacheController {

    private static final Logger logger = LoggerFactory.getLogger(CacheController.class);

    private static boolean statisticsEnabled = false;
    private EntityManagerFactory emf;

    public CacheController() {
        EnvLoader.loadEnv();
        this.emf = Persistence.createEntityManagerFactory("labwork_pu");
    }

    @GET
    @Path("/statistics")
    public Response getStatistics() {
        logger.info("Вызван метод показа статистики.");
        if (emf == null) {
            try {
                EnvLoader.loadEnv();
                this.emf = Persistence.createEntityManagerFactory("labwork_pu");
            } catch (Exception e) {
                return Response.ok("Не получилось создать EntityManagerFactory: " + e.getMessage()).build();
            }
        }
        if (emf == null) {
            return Response.ok("EntityManagerFactory опять не создался.").build();
        }
        try {
            SessionFactory sessionFactory = emf.unwrap(SessionFactory.class);
            Statistics stats = sessionFactory.getStatistics();
            StringBuilder result = new StringBuilder();
            result.append("=== HIBERNATE L2 CACHE STATISTICS ===\n");
            result.append("Statistics enabled: ").append(stats.isStatisticsEnabled()).append("\n");
            result.append("\n=== COUNTERS ===\n");
            result.append("Second Level Cache Hits: ").append(stats.getSecondLevelCacheHitCount()).append("\n");
            result.append("Second Level Cache Misses: ").append(stats.getSecondLevelCacheMissCount()).append("\n");
            result.append("Second Level Cache Puts: ").append(stats.getSecondLevelCachePutCount()).append("\n");
            result.append("Query Cache Hits: ").append(stats.getQueryCacheHitCount()).append("\n");
            result.append("Query Cache Misses: ").append(stats.getQueryCacheMissCount()).append("\n");
            return Response.ok(result.toString()).build();
        } catch (Exception e) {
            return Response.ok("Ошибка при показе статистики: " + e.getMessage()).build();
        }
    }

    @POST
    @Path("/logging/enable")
    public Response enableLogging() {
        logger.info("Включение ведения статистики кэшей.");
        statisticsEnabled = true;
        if (emf == null) {
            try {
                EnvLoader.loadEnv();
                this.emf = Persistence.createEntityManagerFactory("labwork_pu");
            } catch (Exception e) {
                return Response.ok("Cannot create EntityManagerFactory: " + e.getMessage()).build();
            }
        }
        if (emf != null) {
            try {
                SessionFactory sessionFactory = emf.unwrap(SessionFactory.class);
                sessionFactory.getStatistics().setStatisticsEnabled(true);
                return Response.ok("Статистика кэшей теперь включена.\n" +
                        "Теперь статистика будет собираться для L2 кэша").build();
            } catch (Exception e) {
                return Response.ok("Ошибеа во время включения стаистики: " + e.getMessage()).build();
            }
        }
        return Response.ok("Не удается собирать статистику.").build();
    }

    @POST
    @Path("/logging/disable")
    public Response disableLogging() {
        logger.info("Отключения стаистики кэша.");
        statisticsEnabled = false;
        if (emf != null) {
            try {
                SessionFactory sessionFactory = emf.unwrap(SessionFactory.class);
                sessionFactory.getStatistics().setStatisticsEnabled(false);
                return Response.ok("Ведение статистики кэша отключено.").build();
            } catch (Exception e) {
                return Response.ok("Ошибка во время отключения: " + e.getMessage()).build();
            }
        }
        return Response.ok("Статистика кэшей отключена.").build();
    }

    @DELETE
    @Path("/clear")
    public Response clearAllCaches() {
        logger.info("Очистка кэшей.");
        if (emf == null) {
            try {
                EnvLoader.loadEnv();
                this.emf = Persistence.createEntityManagerFactory("labwork_pu");
            } catch (Exception e) {
                return Response.ok("Не удалось создать EntityManagerFactory: " + e.getMessage()).build();
            }
        }
        if (emf != null) {
            try {
                SessionFactory sessionFactory = emf.unwrap(SessionFactory.class);
                sessionFactory.getCache().evictAll();
                return Response.ok("Все кэши очищены.").build();
            } catch (Exception e) {
                return Response.ok("Ошибка во время очистки кэшей: " + e.getMessage()).build();
            }
        }
        return Response.ok("Не удалось очистить кэш.").build();
    }
}