package ehealth.group1.backend.service;

import ehealth.group1.backend.dto.Settings;
import ehealth.group1.backend.enums.ECGSTATE;
import ehealth.group1.backend.helper.ECGStateHolder;
import ehealth.group1.backend.persistence.DataDao;
import ehealth.group1.backend.persistence.SettingsDao;
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
  SettingsDao settingsDao;

  private Settings settings;
  private final DataService dataService;
  private final AnalyserService analyserService;
  private ECGStateHolder ecgStateHolder;

  public ECGService(DataDao dataDao, SettingsDao settingsDao, DataService dataService, AnalyserService analyserService){
    this.dataDao = dataDao;
    this.settingsDao = settingsDao;
    this.dataService = dataService;
    this.analyserService = analyserService;
    // TODO: Use production settings
    settings = settingsDao.getForUserId(0L).get(0);
    ecgStateHolder = new ECGStateHolder(settings.ecgStateHolderSettings());
  }

  public List<String> getThing(){
    return dataDao.getThing();
  }

  public void processECG(String data) {
    Observation observation = dataService.getObservation(data);
    LOGGER.info("Starting analysis of ecg observation");
    ECGSTATE currentState = analyserService.analyse(observation, settings.ecgAnalysisSettings());

    ecgStateHolder.update(currentState, observation);

    LOGGER.info("currentState of stateHolder: " + currentState.toString());

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
    ecgStateHolder = new ECGStateHolder(settings.ecgStateHolderSettings());
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