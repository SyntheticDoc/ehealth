package ehealth.group1.backend.service;

import ehealth.group1.backend.enums.ECGSTATE;
import ehealth.group1.backend.helper.ECGStateHolder;
import ehealth.group1.backend.persistence.EKGDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@SuppressWarnings("EnhancedSwitchMigration")
@Component
public class EKGService {
  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  EKGDao ekgDao;

  private final DataService dataService;
  private final AnalyserService analyserService;
  private ECGStateHolder ecgStateHolder = new ECGStateHolder();

  public EKGService(EKGDao ekgDao, DataService dataService, AnalyserService analyserService){
    this.ekgDao = ekgDao;
    this.dataService = dataService;
    this.analyserService = analyserService;
  }

  public String getThing(){
    return ekgDao.getThing();
  }

  public void processECG(String data) {
    ECGSTATE currentState = analyserService.analyse(dataService.getObservation(data));

    ecgStateHolder.update(currentState);

    switch(ecgStateHolder.getCurrent()) {
      case WARNING:
        LOGGER.warn("ECGSTATE is WARNING in currently analysed data!");
        break;
      case CRITICAL:
        LOGGER.warn("CRITICAL ECGSTATE DETECTED! Starting user alert routine...");
        // TODO: Forward to frontend to warn user
        break;
      case CALLEMERGENCY:
        LOGGER.warn("User did not react to critical ecg state. Initiating EMERGENCY CALL!");
        // TODO: Start emergency call
        break;
    }
  }

  public void abortEmergencyCall() {
    LOGGER.warn("ECG emergency call aborted by user!");
    ecgStateHolder = new ECGStateHolder();
  }

}
