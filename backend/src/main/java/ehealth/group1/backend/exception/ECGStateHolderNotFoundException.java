package ehealth.group1.backend.exception;

public class ECGStateHolderNotFoundException extends RuntimeException {
    public ECGStateHolderNotFoundException() {
        super();
    }

    public ECGStateHolderNotFoundException(String message) {
        super(message);
    }

    public ECGStateHolderNotFoundException(Throwable cause) {
        super(cause);
    }

    public ECGStateHolderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
