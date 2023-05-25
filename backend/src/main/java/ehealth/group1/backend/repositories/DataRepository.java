package ehealth.group1.backend.repositories;

import ehealth.group1.backend.entity.ECGData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DataRepository extends JpaRepository<ECGData, Long> {
    @Query("SELECT d FROM ECGData d WHERE (d.timestamp >= ?1 AND d.timestamp <= ?2)")
    List<ECGData> getAllFromTimestampToTimestamp(LocalDateTime start, LocalDateTime end);
}
