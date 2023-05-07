package ehealth.group1.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter @Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;

    private String address;
    private Long phone;
    private boolean emergency;
    private String password;

    // Lead info
    @OneToMany(cascade = CascadeType.ALL)
    private List<ECGDevice> devices = new ArrayList<>();

    public User(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public User(String name, String address, Long phone, boolean emergency, String password) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.emergency = emergency;
        this.password = password;
    }

    public User(String name, String address, Long phone, boolean emergency, String password, ArrayList<ECGDevice> devices) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.emergency = emergency;
        this.password = password;
        this.devices = devices;
    }

    public void addECGDevice(ECGDevice device) {
        devices.add(device);
    }

    @Override
    public String toString() {
        return "User[id=" + id + ",name=" + name + ",address=" + address + ",phone=" + phone + ",emergency=" + emergency +
                ",password=" + password + ", ecgDevices=" + (devices.size() == 0 ? "null" : Arrays.toString(devices.toArray())) + "]";
    }
}