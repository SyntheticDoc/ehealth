package ehealth.group1.backend.persistence;

import ehealth.group1.backend.dto.ECGAnalysisSettings;
import ehealth.group1.backend.dto.ECGStateHolderSettings;
import ehealth.group1.backend.dto.Settings;
import ehealth.group1.backend.exception.UserIdMismatchException;
import ehealth.group1.backend.helper.ErrorHandler;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class SettingsDao {
    private final JdbcTemplate jdbcTemplate;
    private final ErrorHandler errorHandler;

    private static final String TABLE_NAME_SETTINGS = "settings";
    private static final String TABLE_NAME_STATEHOLDER = "settings_ecgstateholder";
    private static final String TABLE_NAME_ANALYSIS = "settings_ecganalysis";
    private static final String SQL_SELECT_ALL = "SELECT DISTINCT * FROM " + TABLE_NAME_SETTINGS;
    private static final String SQL_SELECT_ONE_BY_USER_ID = "SELECT DISTINCT * FROM " + TABLE_NAME_SETTINGS + " WHERE user_id=?";
    private static final String SQL_SELECT_STATEHOLDER_BY_USER_ID = "SELECT DISTINCT * FROM " + TABLE_NAME_STATEHOLDER + " WHERE user_id=?";
    private static final String SQL_SELECT_ANALYSIS_BY_USER_ID = "SELECT DISTINCT * FROM " + TABLE_NAME_ANALYSIS + " WHERE user_id=?";

    public SettingsDao(JdbcTemplate jdbcTemplate, ErrorHandler errorHandler){
        this.jdbcTemplate = jdbcTemplate;
        this.errorHandler = errorHandler;
    }

    public Settings getTestSetting() {
        return new Settings(0L, 0L,
                new ECGStateHolderSettings(
                        1L, 0L, 5, 30
                ),
                new ECGAnalysisSettings(
                        2L, 0L, 5, 10
                ));
    }

    public List<Settings> getForUserId(Long userID) {
        try {
            return jdbcTemplate.query(SQL_SELECT_ONE_BY_USER_ID, this::mapRow, userID);
        } catch(DataAccessException e) {
            errorHandler.handleCustomException("SettingsDao.getForUserId(" + userID + ")", "Could not get user settings for user " + userID, e);
            throw e;
        }
    }

    public List<ECGStateHolderSettings> getStateholderSettingsById(Long id) {
        try {
            return jdbcTemplate.query(SQL_SELECT_STATEHOLDER_BY_USER_ID, this::mapRow_stateholder, id);
        } catch(DataAccessException e) {
            errorHandler.handleCustomException("SettingsDao.getOneById(" + id + ")", "Could not get ECGStateHolderSettings", e);
            throw e;
        }
    }

    public List<ECGAnalysisSettings> getAnalysisSettingsById(Long id) {
        try {
            return jdbcTemplate.query(SQL_SELECT_ANALYSIS_BY_USER_ID, this::mapRow_analysis, id);
        } catch(DataAccessException e) {
            errorHandler.handleCustomException("SettingsDao.getOneById(" + id + ")", "Could not get ECGAnalysisSettings", e);
            throw e;
        }
    }

    private Settings mapRow(ResultSet result, int rownum) throws SQLException {
        Long id = result.getLong("id");
        Long user_id = result.getLong("user_id");
        Long ecgstateholder_id = result.getLong("ecgstateholder_settings");
        Long ecganalysis_id = result.getLong("ecganalysis_settings");
        ECGStateHolderSettings ecgStateHolderSettings = getStateholderSettingsById(ecgstateholder_id).get(0);
        ECGAnalysisSettings ecgAnalysisSettings = getAnalysisSettingsById(ecganalysis_id).get(0);

        if(!ecgStateHolderSettings.user_id().equals(user_id)) {
            UserIdMismatchException e = new UserIdMismatchException("ECGStateHolderSettings user id did not match settings user id!");
            errorHandler.handleCustomException("SettingsDao.mapRow()", "ECGStateHolderSettings user id did not match Settings user id! " +
                    "(Stateholder id: " + ecgStateHolderSettings.user_id() + ", Settings id: " + user_id + ")", e);
            throw e;
        }

        if(!ecgAnalysisSettings.user_id().equals(user_id)) {
            UserIdMismatchException e = new UserIdMismatchException("ECGAnalysisSettings user id did not match settings user id!");
            errorHandler.handleCustomException("SettingsDao.mapRow()", "ECGAnalysisSettings user id did not match Settings user id! " +
                    "(Stateholder id: " + ecgAnalysisSettings.user_id() + ", Settings id: " + user_id + ")", e);
            throw e;
        }

        return new Settings(id, user_id, ecgStateHolderSettings, ecgAnalysisSettings);
    }

    private ECGStateHolderSettings mapRow_stateholder(ResultSet result, int rownum) throws SQLException {
        Long id = result.getLong("id");
        Long user_id = result.getLong("user_id");
        int iterations_transition = result.getInt("iterations_transition");
        int iterations_emergency = result.getInt("iterations_emergency");

        return new ECGStateHolderSettings(id, user_id, iterations_transition, iterations_emergency);
    }

    private ECGAnalysisSettings mapRow_analysis(ResultSet result, int rownum) throws SQLException {
        Long id = result.getLong("id");
        Long user_id = result.getLong("user_id");
        int maxDeviation = result.getInt("maxDeviation");
        int maxDeviation_num = result.getInt("maxDeviation_num");

        return new ECGAnalysisSettings(id, user_id, maxDeviation, maxDeviation_num);
    }
}
