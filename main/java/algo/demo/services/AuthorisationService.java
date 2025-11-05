package algo.demo.services;

import algo.demo.PasswordHasher;
import algo.demo.dto.UserLoginRequest;
import algo.demo.exceptions.LoginException;
import algo.demo.exceptions.RegistrationException;
import algo.demo.repository.UserRepository;
import algo.demo.security.JwtUtil;
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

    @Inject
    private JwtUtil jwtUtil;

    public Boolean isNameInUsers(String username) {
        return userRepository.isNameInUse(username);
    }

    public Boolean isDataCorrect(UserLoginRequest userLoginRequest) {
        return userLoginRequest != null && userLoginRequest.password() != null &&
                userLoginRequest.username() != null && !userLoginRequest.password().isEmpty();
    }

    public Boolean isUserCorrect(UserLoginRequest userLoginRequest) {
        if (userRepository.isUserExist(userLoginRequest.username(), userLoginRequest.password())) {
            return true;
        }
        throw new LoginException("Неверный пароль или имя пользователя.");
    }

    public void registerUser(UserLoginRequest userLoginRequest) {
        if (!isNameInUsers(userLoginRequest.username()) && isDataCorrect(userLoginRequest)) {
            try {
                userRepository.registerUser(userLoginRequest.username(), passwordHasher.hash(userLoginRequest.password()));
            } catch (Exception e) {
                logger.error("Что то пошло не так." + e.getMessage());
                throw new RegistrationException("Что то пошло не так." + e.getMessage());
            }
        } else {
            logger.error("Что то пошло не так во время регистрации. Попробуйте другой логин.");
            throw new RegistrationException("Что то пошло не так во время регистрации. Попробуйте другой логин.");
        }
    }

    public String getToken(UserLoginRequest userLoginRequest) {
        if (isDataCorrect(userLoginRequest)) {
            return jwtUtil.generateToken(userLoginRequest.username());
        }
        throw new LoginException("Неверные данные для входа. Проверьте пароль и имя пользовтаеля.");
    }

}
