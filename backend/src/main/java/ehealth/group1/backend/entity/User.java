package ehealth.group1.backend.entity;

public class User {
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

    public User(Long id, String name, String address, Long phone, boolean emergency, String password) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.emergency = emergency;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }



    @Override
    public String toString() {
        return "User[id=" + id + ",name=" + name + "]";
    }
}
