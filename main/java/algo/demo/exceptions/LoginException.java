package algo.demo.exceptions;

public class LoginException extends RuntimeException {
    public LoginException(String message) {
        super("Произошла ошибка во время выхода: " + message);
  }
}
