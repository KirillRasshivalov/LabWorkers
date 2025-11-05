package algo.demo.exceptions;

public class ParsingException extends RuntimeException {
    public ParsingException(String message) {
        super("Произошла ошибка во время парсинга: " + message);
    }
}
