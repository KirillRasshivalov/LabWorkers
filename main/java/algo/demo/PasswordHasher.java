package algo.demo;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {
    private final int logRounds;

    public PasswordHasher() {
        this.logRounds = 12;
    }

    public PasswordHasher(int logRounds) {
        this.logRounds = logRounds;
    }

    public String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(logRounds));
    }
}
