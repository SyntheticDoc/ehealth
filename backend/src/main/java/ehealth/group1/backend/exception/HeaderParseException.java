package ehealth.group1.backend.exception;

public class HeaderParseException extends ParseException {
    /**
     * Construct exception with message and error code.
     *
     * @see RuntimeException
     * @param message exception message
     */
    public HeaderParseException(String message) {
        super(message);
    }
}
