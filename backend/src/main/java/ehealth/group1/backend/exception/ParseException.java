package ehealth.group1.backend.exception;

public class ParseException extends RuntimeException {
    /**
     * Construct exception with message and error code.
     *
     * @see RuntimeException
     * @param message exception message
     */
    public ParseException(String message) {
        super(message);
    }
}
