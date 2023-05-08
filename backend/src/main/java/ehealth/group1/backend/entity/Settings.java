package ehealth.group1.backend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@NoArgsConstructor
@Getter @Setter
public class Settings {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long userId;

    @ColumnDefault("0")
    private int dataWriter_lastFileNum;

    @Getter(AccessLevel.NONE)
    @ColumnDefault("false")
    private boolean writeDataToDisk;

    @Getter(AccessLevel.NONE)
    @ColumnDefault("false")
    private boolean drawEcgData;

    @OneToOne(cascade = CascadeType.ALL)
    private ECGStateHolderSettings ecgStateHolderSettings;

    @OneToOne(cascade = CascadeType.ALL)
    private ECGAnalysisSettings ecgAnalysisSettings;

    public Settings(Long userId, ECGStateHolderSettings ecgStateHolderSettings, ECGAnalysisSettings ecgAnalysisSettings) {
        this.userId = userId;
        this.ecgStateHolderSettings = ecgStateHolderSettings;
        this.ecgAnalysisSettings = ecgAnalysisSettings;
    }

    public boolean writeDataToDisk() {
        return writeDataToDisk;
    }

    public boolean drawEcgData() {
        return drawEcgData;
    }
}
