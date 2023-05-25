package ehealth.group1.backend.exception;

public class DeprecatedException extends RuntimeException {
    public DeprecatedException() {
        super();
    }

    public DeprecatedException(String message) {
        super(message);
    }

    public DeprecatedException(Throwable cause) {
        super(cause);
    }

    public DeprecatedException(String message, Throwable cause) {
        super(message, cause);
    }
}
