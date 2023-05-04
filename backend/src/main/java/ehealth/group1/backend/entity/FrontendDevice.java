package ehealth.group1.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Holds data about the users frontend (smartphone)
@Entity
@NoArgsConstructor
@Getter @Setter
public class FrontendDevice {
    // Internal database id for device
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // Device self-identification string
    private String selfID;

    // Unique identifier for the device
    private String identifier;

    // Display name for the device, for example "Android Smartphone XYZ"
    private String name;

    public FrontendDevice(Long id, String identifier, String name) {
        this.id = id;
        this.identifier = identifier;
        this.name = name;
    }

    @Override
    public String toString() {
        return "FrontendDevice[" +
                "id=" + id +
                "selfID='" + selfID + "'" +
                ",identifier='" + identifier + "'" +
                ",name='" + name + "'" +
                ']';
    }
}
