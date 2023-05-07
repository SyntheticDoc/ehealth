package ehealth.group1.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Override
    public String toString() {
        return "User[id=" + id + ",name=" + name + ",address=" + address + ",phone=" + phone + ",emergency=" + emergency +
                ",password=" + password + "]";
    }
}