package algo.demo.services;

import algo.demo.database.UsersTable;
import algo.demo.enums.Roles;
import algo.demo.exceptions.DefaultException;
import algo.demo.repository.UserRepository;
import algo.demo.security.JwtUtil;
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
            logger.info("Роль успешно выдана.");
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new DefaultException(e.getMessage());
        }
    }

    public boolean isUserAdmin(String username) {
        return userRepository.getRole(username) == Roles.ADMIN;
    }
}
