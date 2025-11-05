package algo.demo.exceptions;

public class DefaultError extends RuntimeException {
    public DefaultError(String message) {
        super("Что то пошло не так: " + message);
    }
}
