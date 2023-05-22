package ehealth.group1.backend.repositories;

import ehealth.group1.backend.entity.ECGAnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ECGAnalysisRepository extends JpaRepository<ECGAnalysisResult, Long> {
}
