package ehealth.group1.backend.repositories;

import ehealth.group1.backend.entity.ECGDevice;
import ehealth.group1.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByNameAndPassword(String name, String password);

    User findByName(String name);

    User findByDevicesContains(ECGDevice device);
}
