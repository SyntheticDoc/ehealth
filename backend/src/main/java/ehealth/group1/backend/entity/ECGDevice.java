package ehealth.group1.backend.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class ECGDevice {
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

    // Number of leads (ecg electrodes) of device
    private int leads;

    // Pin for registering device to user
    private String pin;

    // Lead info
    @OneToMany(cascade = CascadeType.ALL)
    private List<ECGDeviceComponent> components = new ArrayList<>();

    public ECGDevice() {

    }

    public ECGDevice(Long id, String selfID, String identifier, String name, int leads, String pin, List<ECGDeviceComponent> components)
            throws IllegalStateException {
        this.id = id;
        this.selfID = selfID;
        this.identifier = identifier;
        this.name = name;
        this.leads = leads;
        this.pin = pin;
        this.components = components;

        if (leads != components.size()) {
            throw new IllegalStateException("ECGDevice(): Leads argument and internal count of leads is not equal [Leads: " +
                    leads + ", component count: " + components.size() + "]. Can't construct Object ECGDevice!");
        }
    }

    public Long getId() {
        return id;
    }

    public String getSelfID() {
        return selfID;
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

    public String getPin() {
        return pin;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSelfID(String selfID) {
        this.selfID = selfID;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLeads(int leads) {
        this.leads = leads;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public List<ECGDeviceComponent> getComponents() {
        return components;
    }

    public void setComponents(List<ECGDeviceComponent> components) {
        this.components = components;
    }

    @Override
    public String toString() {
        return "ECGDevice[" +
                "id=" + id +
                ",selfID='" + selfID + "'" +
                ",identifier='" + identifier + "'" +
                ",name='" + name + "'" +
                ",leads=" + leads +
                ",pin='" + pin + "'" +
                ",components=" + components.toString() +
                ']';
    }
}
