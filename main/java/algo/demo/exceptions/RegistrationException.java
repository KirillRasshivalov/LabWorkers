package algo.demo.exceptions;

public class RegistrationException extends RuntimeException {
    public RegistrationException(String message) {

        super("Произошла ошибка во время регистрации:" + message);
    }
}
