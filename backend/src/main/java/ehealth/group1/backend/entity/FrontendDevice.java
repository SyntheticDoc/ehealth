package ehealth.group1.backend.entity;

// Holds data about the users frontend (smartphone)
public class FrontendDevice {
    // Internal database id for device
    private Long id;

    // Unique identifier for the device, constructed by the frontend device itself
    private String identifier;

    // Display name for the device, for example "Android Smartphone XYZ"
    private String name;

    public FrontendDevice(Long id, String identifier, String name) {
        this.id = id;
        this.identifier = identifier;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "FrontendDevice[" +
                "id=" + id +
                ",identifier='" + identifier +
                ",name='" + name +
                ']';
    }
}
