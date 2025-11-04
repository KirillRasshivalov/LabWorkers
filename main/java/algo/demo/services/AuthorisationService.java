package algo.demo.services;

import algo.demo.PasswordHasher;
import algo.demo.dto.UserLoginRequest;
import algo.demo.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ApplicationScoped
public class AuthorisationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthorisationService.class.getName());

    private final PasswordHasher passwordHasher = new PasswordHasher();

    @Inject
    private UserRepository userRepository;

    public Boolean isNameInUsers(String username) {
        return userRepository.isNameInUse(username);
    }

    public Boolean isDataCorrect(UserLoginRequest userLoginRequest) {
        return userLoginRequest != null && userLoginRequest.password() != null &&
                userLoginRequest.username() != null && !userLoginRequest.password().isEmpty();
    }

    public Boolean isUserCorrect(UserLoginRequest userLoginRequest) {
        return userRepository.isUserExist(userLoginRequest.username(), userLoginRequest.password());
    }

    public void registerUser(UserLoginRequest userLoginRequest) {
        if (!isNameInUsers(userLoginRequest.username())) {
            try {
                userRepository.registerUser(userLoginRequest.username(), passwordHasher.hash(userLoginRequest.password()));
            } catch (Exception e) {
                logger.error("Что то пошло не так." + e.getMessage());
            }
        } else logger.error("Что то пошло не так во время регистрации. Попробуйте другой логин.");
    }
}
