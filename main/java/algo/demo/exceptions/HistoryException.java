package algo.demo.exceptions;

public class HistoryException extends RuntimeException {
    public HistoryException(String message) {
        super("Произошла ошибка при показе списка импортов: " + message);
    }
}
