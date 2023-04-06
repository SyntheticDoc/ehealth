package ehealth.group1.backend.service;

import ehealth.group1.backend.persistence.EKGDao;
import org.springframework.stereotype.Component;

@Component
public class EKGService {
  EKGDao ekgDao;

  public EKGService(EKGDao ekgDao){
    this.ekgDao = ekgDao;
  }

  public String getThing(){
    return ekgDao.getThing();
  }

}
