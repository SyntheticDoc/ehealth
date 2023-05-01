package ehealth.group1.backend.repositories;

import ehealth.group1.backend.entity.ECGDevice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<ECGDevice, Long> {

    ECGDevice findECGDeviceByNameAndPin(String name, String pin);
}
