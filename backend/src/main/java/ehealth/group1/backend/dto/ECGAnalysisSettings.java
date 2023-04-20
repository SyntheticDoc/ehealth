package ehealth.group1.backend.dto;

public record ECGAnalysisSettings(Long id, Long user_id, int maxDeviation, int maxDeviationNum) {
}
