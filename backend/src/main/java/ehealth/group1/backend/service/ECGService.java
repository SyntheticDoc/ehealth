package ehealth.group1.backend.service;

import ehealth.group1.backend.enums.ECGSTATE;
import ehealth.group1.backend.helper.ECGStateHolder;
import ehealth.group1.backend.persistence.DataDao;
import org.hl7.fhir.r5.model.Observation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.List;

@SuppressWarnings("EnhancedSwitchMigration")
@Component
public class ECGService {
  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  DataDao dataDao;

  private final DataService dataService;
  private final AnalyserService analyserService;
  private ECGStateHolder ecgStateHolder = new ECGStateHolder();

  public ECGService(DataDao dataDao, DataService dataService, AnalyserService analyserService){
    this.dataDao = dataDao;
    this.dataService = dataService;
    this.analyserService = analyserService;
  }

  public List<String> getThing(){
    return dataDao.getThing();
  }

  public void processECG(String data) {
    Observation observation = dataService.getObservation(data);
    ECGSTATE currentState = analyserService.analyse(observation);

    ecgStateHolder.update(currentState, observation);

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

  public String getData(int component) {
    return ecgStateHolder.getCurrentObservation().getComponent().get(component).getValueSampledData().getData();
  }

  public String getData() {
    return ecgStateHolder.getCurrentObservation().getComponent().get(0).getValueSampledData().getData();
  }

  public String getData(String data) {
    return data;
  }

}
