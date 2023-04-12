package ehealth.group1.backend.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EKGDao {

  private final JdbcTemplate jdbcTemplate;

  public EKGDao(JdbcTemplate jdbcTemplate){
    this.jdbcTemplate = jdbcTemplate;
  }


  public List<String> getThing(){
    List<String> result = new ArrayList<>();
    try {
      result =  jdbcTemplate.query("Select * from test", this::mapRow);
    } catch (DataAccessException e) {
      result.add("Fehler mit Datenbankverbindung");
    }
    return result;
  }

  private String mapRow(ResultSet result, int rownum) throws SQLException {
    return String.valueOf(result.getLong("id"));
  }
}
