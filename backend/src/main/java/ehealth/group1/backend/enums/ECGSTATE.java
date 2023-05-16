package ehealth.group1.backend.enums;

/**
 * Enum to represent the analysis result of an ecg.
 *
 * OK - no abnormalities detected
 * WARNING - some abnormalities detected, waiting for more data to verify
 * CRITICAL - lots of abnormalities detected in the last few datasets, indicates a potentially critical ecg state
 * CALLEMERGENCY - state was critical and user hasn't reacted, possibly due to a serious medical problem, an ambulance should be called immediately
 * INVALID - indicates that this dataset contains invalid data, for example if a lead has come off or the data was malformed
 */
public enum ECGSTATE {
    OK,
    WARNING,
    CRITICAL,
    CALLEMERGENCY,
    INVALID
}
