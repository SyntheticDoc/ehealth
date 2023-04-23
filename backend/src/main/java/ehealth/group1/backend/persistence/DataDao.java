package ehealth.group1.backend.persistence;
import ehealth.group1.backend.entity.User;
import ehealth.group1.backend.exception.PersistenceException;
import ehealth.group1.backend.helper.ErrorHandler;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DataDao {
  private final JdbcTemplate jdbcTemplate;
  private final ErrorHandler errorHandler;
  private final UserDao userDao;

  private static final String TABLE_NAME_TEST = "test";
  private static final String SQL_SELECT_ALL_TEST = "SELECT DISTINCT * FROM " + TABLE_NAME_TEST;

  public DataDao(JdbcTemplate jdbcTemplate, ErrorHandler errorHandler, UserDao userDao){
    this.jdbcTemplate = jdbcTemplate;
    this.errorHandler = errorHandler;
    this.userDao = userDao;
  }

  public List<User> getAllUsers(Long maxRows) {
    return userDao.getAllUsers(maxRows);
  }

  public List<User> getOneById(Long id) {
    return userDao.getOneById(id);
  }

  public void createUser(User user) {
    userDao.createUser(user);
  }

  public void updateUser(User user) {
    userDao.updateUser(user);
  }

  public void deleteUser(User user) {
    userDao.deleteUser(user);
  }

  public boolean userExists(Long id) {
    return userDao.userExists(id);
  }

  public List<User> searchUser(User user) {
    return userDao.searchUser(user);
  }

  public List<String> getThing(){
    List<String> result = new ArrayList<>();
    try {
      result =  jdbcTemplate.query(SQL_SELECT_ALL_TEST, this::mapRow_test);
    } catch (DataAccessException e) {
      result.add("Fehler mit Datenbankverbindung");
    }
    return result;
  }

  private String mapRow_test(ResultSet result, int rownum) throws SQLException {
    return String.valueOf(result.getLong("id"));
  }
}
