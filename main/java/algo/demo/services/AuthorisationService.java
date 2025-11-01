package algo.demo.services;

import algo.demo.dto.UserLoginRequest;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class AuthorisationService {
    private Map<String, String> dict;
    private List<String> list;

    public AuthorisationService() {
        dict = new HashMap<>();
        list = new ArrayList<>();
    }

    public Boolean isNameInUsers(String username) {
        return list.contains(username);
    }

    public Boolean isDataCorrect(UserLoginRequest userLoginRequest) {
        return userLoginRequest != null && userLoginRequest.password() != null &&
                userLoginRequest.username() != null && !userLoginRequest.password().isEmpty();
    }

    public Boolean isUserCorrect(UserLoginRequest userLoginRequest) {
        return isNameInUsers(userLoginRequest.username()) &&
                dict.get(userLoginRequest.username()).equals(userLoginRequest.password());
    }

    public void registerUser(UserLoginRequest userLoginRequest) {
        if (!isNameInUsers(userLoginRequest.username())) {
            list.add(userLoginRequest.username());
            dict.put(userLoginRequest.username(), userLoginRequest.password());
            return;
        }
        throw new IllegalArgumentException("Такое имя уже занято.");
    }
}
