package algo.demo.exceptions;

public class DefaultException extends RuntimeException {
    public DefaultException(String message) {
        super("Что то пошло не так: " + message);
    }
}
