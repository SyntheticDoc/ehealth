package ehealth.group1.backend.exception;

public class UserIdMismatchException extends RuntimeException {
    /**
     * Construct exception with message and error code.
     *
     * @see RuntimeException
     * @param message exception message
     */
    public UserIdMismatchException(String message) {
        super(message);
    }
}
