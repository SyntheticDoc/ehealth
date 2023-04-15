package ehealth.group1.backend.exception;

/**
 * Signals that the interval unit of a component of an ecg observation didn't match a known interval unit
 */
public class InvalidIntervalUnitException extends RuntimeException {
    /**
     * Construct exception with message and error code.
     *
     * @see RuntimeException
     * @param message exception message
     */
    public InvalidIntervalUnitException(String message) {
        super(message);
    }
}
