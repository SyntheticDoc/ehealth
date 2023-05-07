package ehealth.group1.backend.repositories;

import ehealth.group1.backend.entity.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingsRepository extends JpaRepository<Settings, Long> {

    Settings findByUserId(long userId);

    Settings deleteByUserId(long userId);
}
