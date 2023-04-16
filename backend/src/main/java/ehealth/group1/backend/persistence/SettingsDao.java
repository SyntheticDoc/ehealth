package ehealth.group1.backend.persistence;

import ehealth.group1.backend.dto.ECGAnalysisSettings;
import ehealth.group1.backend.dto.ECGStateHolderSettings;
import ehealth.group1.backend.dto.Settings;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SettingsDao {
    private final JdbcTemplate jdbcTemplate;

    private static final String TABLE_NAME = "settings";
    private static final String SQL_SELECT_ALL = "SELECT DISTINCT * FROM " + TABLE_NAME;

    public SettingsDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public Settings getTestSetting() {
        return new Settings(0L,
                new ECGStateHolderSettings(
                        1L, 5, 30
                ),
                new ECGAnalysisSettings(
                        2L, 5, 10
                ));
    }
}
