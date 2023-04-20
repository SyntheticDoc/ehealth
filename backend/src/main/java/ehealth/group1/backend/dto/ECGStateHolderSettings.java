package ehealth.group1.backend.dto;

public record ECGStateHolderSettings(Long id, Long user_id, int iterationsToStateTransition, int iterationsToEmergencyCall) {
}
