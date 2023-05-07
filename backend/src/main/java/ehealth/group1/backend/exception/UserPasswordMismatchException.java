package ehealth.group1.backend.exception;

public class UserPasswordMismatchException extends RuntimeException {
    public UserPasswordMismatchException() {
        super();
    }

    public UserPasswordMismatchException(String message) {
        super(message);
    }

    public UserPasswordMismatchException(Throwable cause) {
        super(cause);
    }

    public UserPasswordMismatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
