package ehealth.group1.backend.entity;

public class ECGDevice {
    // Internal database id for device
    private Long id;

    // Unique identifier for the device, constructed by the frontend device itself
    private String identifier;

    // Display name for the device, for example "Custom Arduino ECG"
    private String name;

    // Number of leads (ecg electrodes) of device
    private int leads;

    public ECGDevice(Long id, String identifier, String name, int leads) {
        this.id = id;
        this.identifier = identifier;
        this.name = name;
        this.leads = leads;
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

    public int getLeads() {
        return leads;
    }

    @Override
    public String toString() {
        return "ECGDevice[" +
                "id=" + id +
                ",identifier='" + identifier +
                ",name='" + name +
                ",leads='" + leads +
                ']';
    }
}
