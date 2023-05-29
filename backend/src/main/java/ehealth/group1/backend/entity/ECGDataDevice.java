package ehealth.group1.backend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for transmitting ecg data wrapped in a ecgDevice to and from the database. Uses ECGDataComponent to represent
 * each component (lead) of the ecg.
 */
@Entity
@NoArgsConstructor
@Getter
@Setter
public class ECGDataDevice {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private LocalDateTime timestamp;

    @Transient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm:ss:SSS");

    private String deviceName;
    private String deviceIdentifier;

    @OneToMany(cascade = CascadeType.ALL)
    private List<ECGDataComponent> components = new ArrayList<>();

    public ECGDataDevice(LocalDateTime timestamp, String deviceName, String deviceIdentifier, List<ECGDataComponent> components) {
        this.timestamp = timestamp;
        this.deviceName = deviceName;
        this.deviceIdentifier = deviceIdentifier;
        this.components = components;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        result.append("ECGData[id=").append(id).append(",");
        result.append(" timestamp='").append(timestamp.format(dtf)).append("',");
        result.append(" deviceName='").append(deviceName).append("',");
        result.append(" deviceIdentifier='").append(deviceIdentifier).append("',");
        result.append(" components=");

        for(int i = 0; i < components.size(); i++) {
            result.append(components.get(i).toStringShort());

            if(i < (components.size()) - 1) {
                result.append(",");
            }
        }

        result.append("]");

        return result.toString();
    }
}
