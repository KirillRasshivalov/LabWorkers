package algo.demo.services;

import algo.demo.cache.RedisClientProducer;
import algo.demo.database.UsersTable;
import algo.demo.enums.Roles;
import algo.demo.exceptions.DefaultException;
import algo.demo.repository.UserRepository;
import algo.demo.security.JwtUtil;
import io.lettuce.core.api.sync.RedisCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ApplicationScoped
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class.getName());

    @Inject
    private JwtUtil jwtUtil;

    @Inject
    private UserRepository userRepository;

    @Inject
    private RedisCommands<String, String> redis;

    public String getUsername(String token) {
        return jwtUtil.extractUsername(token);
    }

    public List<UsersTable> getAllUsers() {
        try {
            return userRepository.getUsersOfSystem();
        } catch (Exception e) {
            throw new DefaultException(e.getMessage());
        }
    }

    public void assignRoleToUser(String username) {
        try {
            userRepository.setRole(username, Roles.ADMIN);
            invalidateAdminCache(username);
            logger.info("Роль успешно выдана.");
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new DefaultException(e.getMessage());
        }
    }

    public boolean isUserAdmin(String username) {
        String cacheKey = "user:admin:" + username;
        try {
            String cached = redis.get(cacheKey);
            if (cached != null) {
                return Boolean.parseBoolean(cached);
            }
        } catch (Exception e) {
            logger.error("Ошибка с редисом: " + e.getMessage());
            throw new DefaultException(e.getMessage());
        }
        boolean isAdmin = userRepository.getRole(username) == Roles.ADMIN;
        try {
            redis.setex(cacheKey, 300, String.valueOf(isAdmin));
        } catch (Exception e) {
            logger.error("Ошибка с редисом: " + e.getMessage());
            throw new DefaultException(e.getMessage());
        }
        return isAdmin;
    }

    private void invalidateAdminCache(String username) {
        if (username != null && !username.trim().isEmpty()) {
            String key = "user:admin:" + username;
            redis.del(key);
        }
    }
}
