package ehealth.group1.backend.entity;

import jakarta.persistence.*;

@Entity
public class Settings {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long userId;

    @OneToOne(cascade = CascadeType.ALL)
    private ECGStateHolderSettings ecgStateHolderSettings;

    @OneToOne(cascade = CascadeType.ALL)
    private ECGAnalysisSettings ecgAnalysisSettings;

    public Settings(Long userId, ECGStateHolderSettings ecgStateHolderSettings, ECGAnalysisSettings ecgAnalysisSettings) {
        this.userId = userId;
        this.ecgStateHolderSettings = ecgStateHolderSettings;
        this.ecgAnalysisSettings = ecgAnalysisSettings;
    }

    public Settings() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public ECGStateHolderSettings getEcgStateHolderSettings() {
        return ecgStateHolderSettings;
    }

    public void setEcgStateHolderSettings(ECGStateHolderSettings ecgStateHolderSettings) {
        this.ecgStateHolderSettings = ecgStateHolderSettings;
    }

    public ECGAnalysisSettings getEcgAnalysisSettings() {
        return ecgAnalysisSettings;
    }

    public void setEcgAnalysisSettings(ECGAnalysisSettings ecgAnalysisSettings) {
        this.ecgAnalysisSettings = ecgAnalysisSettings;
    }
}
