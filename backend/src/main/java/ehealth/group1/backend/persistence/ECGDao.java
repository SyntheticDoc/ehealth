package ehealth.group1.backend.persistence;

import ehealth.group1.backend.entity.ECGData;
import ehealth.group1.backend.exception.PersistenceException;
import ehealth.group1.backend.helper.ErrorHandler;
import org.hl7.fhir.r5.model.Observation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.lang.invoke.MethodHandles;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;


@Repository
public class ECGDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final JdbcTemplate jdbcTemplate;
    private final ErrorHandler errorHandler;

    private static final String TABLE_NAME_DATA = "ECGData";
    private static final String TABLE_NAME_COMPONENT = "ECGComponent";
    private static final String TABLE_NAME_COMPONENTDATA = "ECGComponentData";
    private static final String SQL_SELECT_ALL_DATA = "SELECT DISTINCT * FROM " + TABLE_NAME_DATA;
    private static final String SQL_CREATE_ECGDATA = "INSERT INTO " + TABLE_NAME_DATA + " (timestamp, deviceName, componentIds, dataIds)" +
            " VALUES (?, ?, ?, ?);";
    private static final String SQL_CREATE_ECGCOMPONENT = "INSERT INTO " + TABLE_NAME_COMPONENT + " (test)" +
            " VALUES (?);";
    private static final String SQL_CREATE_ECGCOMPONENTDATA = "INSERT INTO " + TABLE_NAME_COMPONENTDATA + " (test)" +
            " VALUES (?);";

    public ECGDao(JdbcTemplate jdbcTemplate, ErrorHandler errorHandler) {
        this.jdbcTemplate = jdbcTemplate;
        this.errorHandler = errorHandler;
    }

    public void createECGData(Observation o, int checksum, LocalDateTime timestamp) {
        int updateRowsAffected;
        ArrayList<Long> componentIds = new ArrayList<>();
        ArrayList<Long> dataIds = new ArrayList<>();
        char ch = 'a' - 1;

        // LOGGER.info("Begin saving ecg data");

        for(Observation.ObservationComponentComponent c : o.getComponent()) {
            componentIds.add(createECGComponent(c, "" + ch++));
            dataIds.add(createECGComponentData("" + ch++));
        }

        try {
            updateRowsAffected = jdbcTemplate.update(SQL_CREATE_ECGDATA, timestamp, o.getCode().addCoding().getDisplay(),
                    componentIds.toArray(), dataIds.toArray());

            if (updateRowsAffected == 0) {
                PersistenceException e = new PersistenceException("Could not create new ECGData, no specific error was thrown, but no new record was created");
                errorHandler.handleCustomException("ECGDao.createECGData()", "Could not create ECGData", e);
                throw e;
            }

            if (updateRowsAffected > 1) {
                PersistenceException e = new PersistenceException("Could not create new ECGData, no specific error was thrown, but several records were created! " +
                        "Please contact database administrator immediately!");
                errorHandler.handleCustomException("ECGDao.createECGData()", "Could not create ECGData", e);
                throw e;
            }
        } catch (DataAccessException e) {
            PersistenceException ex = new PersistenceException("Could not create ECGData", e);
            errorHandler.handleCustomException("ECGDao.createECGData()", "Could not create ECGData", ex);
            throw ex;
        }
    }

    private long createECGComponent(Observation.ObservationComponentComponent c, String test) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        int updateRowsAffected;

        try {
            updateRowsAffected = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(SQL_CREATE_ECGCOMPONENT, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, LocalDateTime.now().toString());
                return ps;
            }, keyHolder);

            if (updateRowsAffected == 0) {
                PersistenceException e = new PersistenceException("Could not create new ECGComponent, no specific error was thrown, but no new record was created");
                errorHandler.handleCustomException("ECGDao.createECGComponent()", "Could not create ECGComponent", e);
                throw e;
            }

            if (updateRowsAffected > 1) {
                PersistenceException e = new PersistenceException("Could not create new ECGComponent, no specific error was thrown, but several records were created! " +
                        "Please contact database administrator immediately!");
                errorHandler.handleCustomException("ECGDao.createECGComponent()", "Could not create ECGComponent", e);
                throw e;
            }
        } catch (DataAccessException e) {
            PersistenceException ex = new PersistenceException("Could not create ECGComponent", e);
            errorHandler.handleCustomException("ECGDao.createECGComponent()", "Could not create ECGComponent", ex);
            throw ex;
        }

        return keyHolder.getKey().longValue();
    }

    private long createECGComponentData(String data) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        int updateRowsAffected;

        try {
            updateRowsAffected = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(SQL_CREATE_ECGCOMPONENTDATA, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, data);
                return ps;
            }, keyHolder);

            if (updateRowsAffected == 0) {
                PersistenceException e = new PersistenceException("Could not create new ECGComponentData, no specific error was thrown, but no new record was created");
                errorHandler.handleCustomException("ECGDao.createECGComponentData()", "Could not create ECGComponentData", e);
                throw e;
            }

            if (updateRowsAffected > 1) {
                PersistenceException e = new PersistenceException("Could not create new ECGComponentData, no specific error was thrown, but several records were created! " +
                        "Please contact database administrator immediately!");
                errorHandler.handleCustomException("ECGDao.createECGComponentData()", "Could not create ECGComponentData", e);
                throw e;
            }
        } catch (DataAccessException e) {
            PersistenceException ex = new PersistenceException("Could not create ECGComponentData", e);
            errorHandler.handleCustomException("ECGDao.createECGComponentData()", "Could not create ECGComponentData", ex);
            throw ex;
        }

        return keyHolder.getKey().longValue();
    }

    /*public boolean componentExists(Long id) {
        Boolean hasRecord;

        try {
            hasRecord = jdbcTemplate.query(SQL_USER_EXISTS, new Object[]{id}, (ResultSet rs) -> rs.next());
            return hasRecord != null && hasRecord;
        } catch (DataAccessException e) {
            PersistenceException ex = new PersistenceException("Could not check if component exists", e);
            errorHandler.handleCustomException("ECGDao.componentExists(" + id + ")", "Could not check if id exists for user", ex);
            throw ex;
        }
    }*/

    private ECGData mapRow_data(ResultSet result, int rownum) throws SQLException {
        /*Long id = result.getLong("id");
        String name = result.getString("name");
        String address = result.getString("address");
        Long phone = result.getLong("phone");
        boolean emergency = result.getBoolean("emergency");
        String password = result.getString("password");*/

        return null;
    }
}
