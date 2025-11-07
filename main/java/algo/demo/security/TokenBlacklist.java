package algo.demo.security;

import algo.demo.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TokenBlacklist {

    @Inject
    private UserRepository userRepository;

    @Inject
    private JwtUtil jwtUtil;


    public boolean isBlacklisted(String token) {
        return !jwtUtil.extractRole(token).equals(userRepository.getRole(jwtUtil.extractUsername(token)));
    }

}
