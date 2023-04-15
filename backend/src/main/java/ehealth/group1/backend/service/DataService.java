package ehealth.group1.backend.service;

import ehealth.group1.backend.persistence.DataDao;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataService {
  DataDao ekgDao;

  public DataService(DataDao ekgDao){
    this.ekgDao = ekgDao;
  }

  public List<String> getThing(){
    return ekgDao.getThing();
  }

  public String getData(String data){
    return data;
  }

}
