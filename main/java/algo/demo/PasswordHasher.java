package algo.demo;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {

    private final int logRounds = 12;

    public String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(logRounds));
    }

    public boolean verify(String password, String hash) {
        try {
            return BCrypt.checkpw(password, hash);
        } catch (Exception e) {
            return false;
        }
    }
}
