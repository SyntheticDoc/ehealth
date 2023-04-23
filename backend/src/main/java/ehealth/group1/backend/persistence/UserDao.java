package ehealth.group1.backend.persistence;

import ehealth.group1.backend.entity.User;
import ehealth.group1.backend.exception.PersistenceException;
import ehealth.group1.backend.helper.ErrorHandler;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UserDao {
    private final JdbcTemplate jdbcTemplate;
    private final ErrorHandler errorHandler;

    private static final String TABLE_NAME_USER = "users";
    private static final String SQL_SELECT_ALL_USERS = "SELECT DISTINCT * FROM " + TABLE_NAME_USER;
    private static final String SQL_SELECT_ORDER_BY_ID = "ORDER BY id FETCH FIRST ? ROWS ONLY";
    private static final String SQL_USER_SELECT_ONE_BY_ID = "SELECT * FROM " + TABLE_NAME_USER + " WHERE id=?";
    private static final String SQL_USER_CREATE = "INSERT INTO " + TABLE_NAME_USER + " (name, address, phone, emergency, password)" +
            " VALUES (?, ?, ?, ?, ?);";
    private static final String SQL_USER_UPDATE = "UPDATE " + TABLE_NAME_USER + " SET name=?, address=?, phone=?, " +
            "emergency=?, password=? WHERE id=?";
    private static final String SQL_USER_EXISTS = "SELECT id FROM " + TABLE_NAME_USER + " WHERE id=?";
    private static final String SQL_USER_DELETE = "DELETE FROM " + TABLE_NAME_USER + " WHERE id=?";
    private static final String SQL_SEARCH_USER = "SELECT * FROM " + TABLE_NAME_USER + " WHERE name=? AND address=? " +
            "AND phone=? AND password=?";

    public UserDao(JdbcTemplate jdbcTemplate, ErrorHandler errorHandler){
        this.jdbcTemplate = jdbcTemplate;
        this.errorHandler = errorHandler;
    }

    public List<User> getAllUsers(Long maxRows) {
        try {
            return jdbcTemplate.query(SQL_SELECT_ALL_USERS + " " + SQL_SELECT_ORDER_BY_ID, this::mapRow_user, maxRows);
        } catch(DataAccessException e) {
            errorHandler.handleCustomException("UserDao.getAllUsers(" + maxRows + ")", "Could not query all users", e);
            throw e;
        }
    }

    public List<User> getOneById(Long id) {
        try {
            return jdbcTemplate.query(SQL_USER_SELECT_ONE_BY_ID, this::mapRow_user, id);
        } catch(DataAccessException e) {
            errorHandler.handleCustomException("UserDao.getOneById(" + id + ")", "Could not get user", e);
            throw e;
        }
    }

    public void createUser(User user) {
        int updateRowsAffected;

        try {
            updateRowsAffected = jdbcTemplate.update(SQL_USER_CREATE, user.getName(), user.getAddress(),
                    user.getPhone(), user.getEmergency(), user.getPassword());

            if (updateRowsAffected == 0) {
                PersistenceException e = new PersistenceException("Could not create new user, no specific error was thrown, but no new record was created");
                errorHandler.handleCustomException("UserDao.createUser(" + user + ")", "Could not create user", e);
                throw e;
            }

            if (updateRowsAffected > 1) {
                PersistenceException e = new PersistenceException("Could not create new user, no specific error was thrown, but several records were created! " +
                        "Please contact database administrator immediately!");
                errorHandler.handleCustomException("UserDao.createUser(" + user + ")", "Could not create user", e);
                throw e;
            }
        } catch (DataAccessException e) {
            PersistenceException ex = new PersistenceException("Could not create user", e);
            errorHandler.handleCustomException("UserDao.createUser(" + user + ")", "Could not create user", ex);
            throw ex;
        }
    }

    public void updateUser(User user) {
        int updateRowsAffected;

        if (!userExists(user.getId())) {
            PersistenceException e = new PersistenceException("Error while updating user: user not found in database! Please try again!");
            errorHandler.handleCustomException("UserDao.updateUser(" + user + ")", "Could not update user, user does not exist", e);
            throw e;
        }

        try {
            updateRowsAffected = jdbcTemplate.update(SQL_USER_UPDATE, user.getName(), user.getAddress(),
                    user.getPhone(), user.getEmergency(), user.getPassword(), user.getId());

            if (updateRowsAffected == 0) {
                PersistenceException e = new PersistenceException("Could not update user, no specific error was thrown, but no new record was created");
                errorHandler.handleCustomException("UserDao.updateUser(" + user + ")", "Could not update user", e);
                throw e;
            }

            if (updateRowsAffected > 1) {
                PersistenceException e = new PersistenceException("Could not update user, no specific error was thrown, but several records were created! " +
                        "Please contact database administrator immediately!");
                errorHandler.handleCustomException("UserDao.updateUser(" + user + ")", "Could not update user", e);
                throw e;
            }
        } catch (DataAccessException e) {
            PersistenceException ex = new PersistenceException("Could not update user", e);
            errorHandler.handleCustomException("UserDao.updateUser(" + user + ")", "Could not update user", ex);
            throw ex;
        }
    }

    public void deleteUser(User user) {
        int deleteRowsAffected;

        if (!userExists(user.getId())) {
            PersistenceException e = new PersistenceException("Error while deleting user: user not found in database! Please try again!");
            errorHandler.handleCustomException("UserDao.deleteUser(" + user + ")", "Could not delete user, user does not exist", e);
            throw e;
        }

        try {
            deleteRowsAffected = jdbcTemplate.update(SQL_USER_DELETE, user.getId());

            if (deleteRowsAffected == 0) {
                PersistenceException e = new PersistenceException("Could not delete user, no specific error was thrown, but no new record was created");
                errorHandler.handleCustomException("UserDao.deleteUser(" + user + ")", "Could not update user", e);
                throw e;
            }

            if (deleteRowsAffected > 1) {
                PersistenceException e = new PersistenceException("Could not delete user, no specific error was thrown, but several records were created! " +
                        "Please contact database administrator immediately!");
                errorHandler.handleCustomException("UserDao.deleteUser(" + user + ")", "Could not delete user", e);
                throw e;
            }
        } catch (DataAccessException e) {
            PersistenceException ex = new PersistenceException("Could not delete user", e);
            errorHandler.handleCustomException("UserDao.deleteUser(" + user + ")", "Could not delete user", ex);
            throw ex;
        }
    }

    public boolean userExists(Long id) {
        Boolean hasRecord;

        try {
            hasRecord = jdbcTemplate.query(SQL_USER_EXISTS, new Object[]{id}, (ResultSet rs) -> rs.next());
            return hasRecord != null && hasRecord;
        } catch (DataAccessException e) {
            PersistenceException ex = new PersistenceException("Could not check if user exists", e);
            errorHandler.handleCustomException("UserDao.userExists(" + id + ")", "Could not check if id exists for user", ex);
            throw ex;
        }
    }

    public List<User> searchUser(User user) {
        try {
            return jdbcTemplate.query(SQL_SEARCH_USER, this::mapRow_user, user.getName(), user.getAddress(), user.getPhone(), user.getPassword());
        } catch (DataAccessException e) {
            PersistenceException ex = new PersistenceException("Could not search for user", e);
            errorHandler.handleCustomException("UserDao.searchUser(" + user + ")", "Could not search for user", ex);
            throw ex;
        }
    }

    private User mapRow_user(ResultSet result, int rownum) throws SQLException {
        Long id = result.getLong("id");
        String name = result.getString("name");
        String address = result.getString("address");
        Long phone = result.getLong("phone");
        boolean emergency = result.getBoolean("emergency");
        String password = result.getString("password");

        return new User(id, name, address, phone, emergency, password);
    }
}
