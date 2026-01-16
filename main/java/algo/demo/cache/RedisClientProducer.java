package algo.demo.cache;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class RedisClientProducer {

    private RedisClient redisClient;
    private StatefulRedisConnection<String, String> connection;

    @PostConstruct
    public void init() {
        redisClient = RedisClient.create("redis://localhost:6379");
        connection = redisClient.connect();
    }

    @Produces
    @ApplicationScoped
    public RedisCommands<String, String> getRedisCommands() {
        return connection.sync();
    }

    @PreDestroy
    public void destroy() {
        if (connection != null) {
            connection.close();
        }
        if (redisClient != null)  redisClient.shutdown();
    }
}
