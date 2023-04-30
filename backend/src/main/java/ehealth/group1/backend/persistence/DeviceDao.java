package ehealth.group1.backend.persistence;

import ehealth.group1.backend.entity.ECGDevice;
import ehealth.group1.backend.entity.ECGDeviceComponent;
import ehealth.group1.backend.exception.PersistenceException;
import ehealth.group1.backend.helper.ErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.lang.invoke.MethodHandles;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
public class DeviceDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final JdbcTemplate jdbcTemplate;
    private final ErrorHandler errorHandler;

    private static final String TABLE_NAME_DEVICES = "ECGDevice";
    private static final String TABLE_NAME_COMPONENT = "ECGComponent";

    private static final String SQL_SELECT_ALL_DEVICES = "SELECT DISTINCT * FROM " + TABLE_NAME_DEVICES;
    private static final String SQL_CREATE_ECGDEVICE = "INSERT INTO " + TABLE_NAME_DEVICES + " (identifier, name, leads, pin, components)" +
            " VALUES (?, ?, ?, ?, ?);";
    private static final String SQL_SELECT_DEVICE_BY_NAME_AND_PIN = "SELECT * FROM " + TABLE_NAME_DEVICES + " WHERE (name=? AND pin=?)";
    private static final String SQL_SELECT_COMPONENT_BY_ID = "SELECT * FROM " + TABLE_NAME_COMPONENT + " WHERE id=?";
    private static final String SQL_CREATE_ECGCOMPONENT = "INSERT INTO " + TABLE_NAME_COMPONENT + " (identifier, name)" +
            " VALUES (?, ?);";

    public DeviceDao(JdbcTemplate jdbcTemplate, ErrorHandler errorHandler) {
        this.jdbcTemplate = jdbcTemplate;
        this.errorHandler = errorHandler;
    }

    public void createECGDevice(ECGDevice device) {
        int updateRowsAffected;
        ArrayList<Long> componentIds = new ArrayList<>();

        for(ECGDeviceComponent c : device.getComponents()) {
            componentIds.add(createECGComponent(c));
        }

        try {
            updateRowsAffected = jdbcTemplate.update(SQL_CREATE_ECGDEVICE, device.getIdentifier(), device.getName(), device.getLeads(),
                    device.getPin(), componentIds.toArray());

            if (updateRowsAffected == 0) {
                PersistenceException e = new PersistenceException("Could not create new ECGDevice, no specific error was thrown, but no new record was created");
                errorHandler.handleCustomException("ECGDao.createECGDevice()", "Could not create ECGDevice", e);
                throw e;
            }

            if (updateRowsAffected > 1) {
                PersistenceException e = new PersistenceException("Could not create new ECGDevice, no specific error was thrown, but several records were created! " +
                        "Please contact database administrator immediately!");
                errorHandler.handleCustomException("ECGDao.createECGDevice()", "Could not create ECGDevice", e);
                throw e;
            }
        } catch (DataAccessException e) {
            PersistenceException ex = new PersistenceException("Could not create ECGDevice", e);
            errorHandler.handleCustomException("ECGDao.createECGDevice()", "Could not create ECGDevice", ex);
            throw ex;
        }
    }

    private long createECGComponent(ECGDeviceComponent c) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        int updateRowsAffected;

        try {
            updateRowsAffected = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(SQL_CREATE_ECGCOMPONENT, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, c.getIdentifier());
                ps.setString(2, c.getName());
                return ps;
            }, keyHolder);

            if (updateRowsAffected == 0) {
                PersistenceException e = new PersistenceException("Could not create new ECGDeviceComponent, no specific error was thrown, but no new record was created");
                errorHandler.handleCustomException("ECGDao.createECGComponent()", "Could not create ECGDeviceComponent", e);
                throw e;
            }

            if (updateRowsAffected > 1) {
                PersistenceException e = new PersistenceException("Could not create new ECGDeviceComponent, no specific error was thrown, but several records were created! " +
                        "Please contact database administrator immediately!");
                errorHandler.handleCustomException("ECGDao.createECGComponent()", "Could not create ECGDeviceComponent", e);
                throw e;
            }
        } catch (DataAccessException e) {
            PersistenceException ex = new PersistenceException("Could not create ECGComponent", e);
            errorHandler.handleCustomException("ECGDao.createECGComponent()", "Could not create ECGDeviceComponent", ex);
            throw ex;
        }

        return keyHolder.getKey().longValue();
    }

    public List<ECGDevice> getDeviceByNameAndPin(String name, String pin) {
        try {
            return jdbcTemplate.query(SQL_SELECT_DEVICE_BY_NAME_AND_PIN, this::mapRow_device, name, pin);
        } catch(DataAccessException e) {
            errorHandler.handleCustomException("DeviceDao.getDeviceByNameAndPin(" + name + ")", "Could not get ecg device with name " + name, e);
            throw e;
        }
    }

    public List<ECGDeviceComponent> getComponentById(Long id) {
        try {
            return jdbcTemplate.query(SQL_SELECT_COMPONENT_BY_ID, this::mapRow_component, id);
        } catch(DataAccessException e) {
            errorHandler.handleCustomException("DeviceDao.getComponentById(" + id + ")", "Could not get ecg device component with id " + id, e);
            throw e;
        }
    }

    private ECGDevice mapRow_device(ResultSet result, int rownum) throws SQLException {
        Long id = result.getLong("id");
        String identifier = result.getString("identifier");
        String name = result.getString("name");
        int leads = result.getInt("leads");
        String pin = result.getString("pin");
        Array component_ids = result.getArray("components");

        LOGGER.warn("mapRow_device, array: " + Arrays.toString(component_ids));
        LOGGER.warn("mapRow_device, array_get: " + result.getArray("components").toString());
        LOGGER.warn("mapRow_device, array_get_get: " + result.getArray("components").getArray().toString());

        ECGDeviceComponent[] components = new ECGDeviceComponent[component_ids.length];

        for(int i = 0; i < component_ids.length; i++) {
            components[i] = getComponentById(component_ids[i]).get(0);
        }

        return new ECGDevice(id, null, identifier, name, leads, pin, components);
    }

    private ECGDeviceComponent mapRow_component(ResultSet result, int rownum) throws SQLException {
        Long id = result.getLong("id");
        String identifier = result.getString("identifier");
        String name = result.getString("name");

        return new ECGDeviceComponent(id, null, identifier, name);
    }
}
