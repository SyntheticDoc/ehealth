package ehealth.group1.backend.service;

import ehealth.group1.backend.persistence.EKGDao;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EKGService {
  EKGDao ekgDao;

  public EKGService(EKGDao ekgDao){
    this.ekgDao = ekgDao;
  }

  public List<String> getThing(){
    return ekgDao.getThing();
  }

}
