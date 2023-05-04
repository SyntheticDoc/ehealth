package ehealth.group1.backend.repositories;

import ehealth.group1.backend.entity.ECGData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ECGDataRepository extends JpaRepository<ECGData, Long> {
}
