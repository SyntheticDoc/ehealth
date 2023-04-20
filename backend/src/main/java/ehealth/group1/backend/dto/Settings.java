package ehealth.group1.backend.dto;

public record Settings(Long id, Long user_id, ECGStateHolderSettings ecgStateHolderSettings, ECGAnalysisSettings ecgAnalysisSettings) {
}
