package algo.demo.services;

import algo.demo.security.JwtUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UserService {

    @Inject
    private JwtUtil jwtUtil;

    public String getUsername(String token) {
        return jwtUtil.extractUsername(token);
    }
}
