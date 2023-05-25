package ehealth.group1.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Used by ECGDevice to represent individual ecg components (leads) of the ECGDevice.
 */
@Entity
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class ECGDeviceComponent implements Serializable {
    // Internal database id for device
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // Device self-identification string
    private String selfID;

    // Unique identifier for the device, internal
    private String identifier;

    // Display name for the device, for example "Custom Arduino ECG"
    private String name;

    @Override
    public String toString() {
        return "ECGDeviceComponent[" +
                "id=" + id +
                ",selfID='" + selfID + "'" +
                ",identifier='" + identifier + "'" +
                ",name='" + name + "'" +
                ']';
    }
}
