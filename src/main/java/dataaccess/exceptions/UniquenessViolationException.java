package dataaccess.exceptions;

public class UniquenessViolationException extends Exception {
    public UniquenessViolationException(String message) {
        super(message);
    }
}