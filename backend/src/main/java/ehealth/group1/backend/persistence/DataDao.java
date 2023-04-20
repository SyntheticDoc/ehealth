package ehealth.group1.backend.persistence;
import ehealth.group1.backend.entity.User;
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

  private static final String TABLE_NAME_USER = "users";
  private static final String TABLE_NAME_TEST = "test";
  private static final String SQL_SELECT_ALL_USERS = "SELECT DISTINCT * FROM " + TABLE_NAME_USER;
  private static final String SQL_SELECT_ALL_TEST = "SELECT DISTINCT * FROM " + TABLE_NAME_TEST;
  private static final String SQL_SELECT_ORDER_BY_ID = "ORDER BY id FETCH FIRST ? ROWS ONLY";
  private static final String SQL_USER_SELECT_ONE_BY_ID = "SELECT * FROM " + TABLE_NAME_USER + " WHERE id=?";

  public DataDao(JdbcTemplate jdbcTemplate, ErrorHandler errorHandler){
    this.jdbcTemplate = jdbcTemplate;
    this.errorHandler = errorHandler;
  }

  public List<User> getAllUsers(Long maxRows) {
    try {
      return jdbcTemplate.query(SQL_SELECT_ALL_USERS + " " + SQL_SELECT_ORDER_BY_ID, this::mapRow_user, maxRows);
    } catch(DataAccessException e) {
      errorHandler.handleCustomException("DataDao.getAllUsers(" + maxRows + ")", "Could not query all users", e);
      throw e;
    }
  }

  public List<User> getOneById(Long id) {
    try {
      return jdbcTemplate.query(SQL_USER_SELECT_ONE_BY_ID, this::mapRow_user, id);
    } catch(DataAccessException e) {
      errorHandler.handleCustomException("DataDao.getOneById(" + id + ")", "Could not get user", e);
      throw e;
    }
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

  private User mapRow_user(ResultSet result, int rownum) throws SQLException {
    Long id = result.getLong("id");
    String name = result.getString("name");

    return new User(id, name);
  }
}
