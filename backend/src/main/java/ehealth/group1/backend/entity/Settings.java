package ehealth.group1.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

/**
 * Container class for all important settings specific to a user. Contains settings for the ECGStateholder, for the analysis
 * of the ecg and the last file number for writing ecg data to disk.
 *
 * If a user has no specific settings, default settings will be loaded for the default user (id: 0L), which is always present
 * in the system.
 */
@Entity
@NoArgsConstructor
@Getter @Setter
@ToString
public class Settings {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long userId;

    @ColumnDefault("0")
    private int dataWriter_lastFileNum;

    @OneToOne(cascade = CascadeType.ALL)
    private ECGStateHolderSettings ecgStateHolderSettings;

    @OneToOne(cascade = CascadeType.ALL)
    private ECGAnalysisSettings ecgAnalysisSettings;

    public Settings(Long userId, ECGStateHolderSettings ecgStateHolderSettings, ECGAnalysisSettings ecgAnalysisSettings) {
        this.userId = userId;
        this.ecgStateHolderSettings = ecgStateHolderSettings;
        this.ecgAnalysisSettings = ecgAnalysisSettings;
    }
}
