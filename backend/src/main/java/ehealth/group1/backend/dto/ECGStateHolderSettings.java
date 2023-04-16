package ehealth.group1.backend.dto;

public record ECGStateHolderSettings(Long id, int iterationsToStateTransition, int iterationsToEmergencyCall) {
}
