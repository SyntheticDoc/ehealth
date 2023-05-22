package ehealth.group1.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

/**
 * Represents an ecg component (lead) of ECGData. This class contains the real ecg data.
 */
@Entity
@NoArgsConstructor
@Getter @Setter
public class ECGDataComponent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String displayName;
    private String componentIdentifier;

    private Double samplingRate;

    @Transient
    private String data;

    public ECGDataComponent(String displayName, String componentIdentifier, Double samplingRate, String data) {
        this.displayName = displayName;
        this.componentIdentifier = componentIdentifier;
        this.samplingRate = samplingRate;
        this.data = data;
    }

    public String toStringShort() {
        return "ECGDataComponent[id=" + id + "]";
    }

    @Override
    public String toString() {
        return "ECGDataComponent[" +
                "id=" + id +
                ", displayName='" + displayName + "'" +
                ", componentIdentifier='" + componentIdentifier + "'" +
                ", samplingRate=" + samplingRate +
                ", data=" + data +
                ']';
    }
}
