package ehealth.group1.backend.service;

import ehealth.group1.backend.customfhirstructures.CustomObservation;
import ehealth.group1.backend.entity.ECGAnalysisResult;
import ehealth.group1.backend.entity.ECGHealthStatus;
import ehealth.group1.backend.entity.Settings;
import ehealth.group1.backend.entity.User;
import ehealth.group1.backend.enums.ECGSTATE;
import ehealth.group1.backend.helper.ECGStateHolder;
import ehealth.group1.backend.repositories.DataRepository;
import ehealth.group1.backend.repositories.SettingsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("EnhancedSwitchMigration")
@Component
public class ECGService {
  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  DataRepository dataRepository;
  SettingsRepository settingsRepository;

  private Settings settings;
  private final DataService dataService;
  private final AnalyserService analyserService;
  private ECGStateHolder ecgStateHolder;

  public ECGService(DataRepository dataRepository, SettingsRepository settingsRepository, DataService dataService, AnalyserService analyserService) {
    this.dataRepository = dataRepository;
    this.settingsRepository = settingsRepository;
    this.dataService = dataService;
    this.analyserService = analyserService;
    // TODO: Use production settings
    settings = settingsRepository.findByUserId(0L);
    ecgStateHolder = new ECGStateHolder(settings.getEcgStateHolderSettings());
  }

  public List<String> getThing() {
    String[] arr = new String[]{"Accessing dataDao.getThing() is not supported anymore"};
    return Arrays.asList(arr);
    //return dataDao.getThing();
  }

  public void processECG(String data) {
    processECGObservation(dataService.getObservation(data));
  }

  public void processECG(CustomObservation data) {
    processECGObservation(dataService.getObservation(data));
  }

  private void processECGObservation(CustomObservation obs) {
    LOGGER.info("Starting analysis of ecg observation");
    ECGSTATE currentState = analyserService.analyse(obs, settings.getEcgAnalysisSettings());

    ecgStateHolder.update(currentState, obs);

    LOGGER.info("currentState of stateHolder: " + ecgStateHolder.getCurrent().toString());

    //dataDao.createECGData(obs, 123, LocalDateTime.now());

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

  public ECGHealthStatus getLastHealthStatus(User user) {
    // TODO: Check user authorization
    CustomObservation obs = ecgStateHolder.getCurrentObservation();
    ECGSTATE ecgState = ecgStateHolder.getCurrent();
    ECGAnalysisResult analysisResult = new ECGAnalysisResult();
    ECGHealthStatus result = new ECGHealthStatus();

    analysisResult.setEcgstate(ecgState);
    analysisResult.setTimestamp(obs.getTimestampAsLocalDateTime());
    analysisResult.setComment("No comment");

    result.setAssociatedUserName(user.getName());
    result.setLastAnalysisResult(analysisResult);

    return result;
  }

  public ECGSTATE getCurrentState() {
    return ecgStateHolder.getCurrent();
  }

  public void abortEmergencyCall() {
    LOGGER.warn("ECG emergency call aborted by user!");
    ecgStateHolder = new ECGStateHolder(settings.getEcgStateHolderSettings());
  }

  /*public ECGData convertToEntity(String data) {
    long idCounter = 0;
    Observation o = dataService.getObservation(data);
    ArrayList<ECGDataComponent> components = new ArrayList<>();

    for(Observation.ObservationComponentComponent comp : o.getComponent()) {
      SampledData sDat = comp.getValueSampledData();
      ECGDataComponent c = new ECGDataComponent(idCounter++, comp.getCode().getCoding().get(0).getDisplay(), sDat.getData());

      c.setOriginValue(sDat.getOrigin().getValue().doubleValue());
      c.setInterval(sDat.getInterval().doubleValue());
      c.setIntervalUnit(sDat.getIntervalUnit());
      c.setFactor(sDat.getFactor().doubleValue());
      c.setLowerLimit(sDat.getLowerLimit().doubleValue());
      c.setUpperLimit(sDat.getUpperLimit().doubleValue());
      c.setDimensions(sDat.getDimensions());

      components.add(c);
    }

    return new ECGData(idCounter, LocalDateTime.now(), o.getDevice().getDisplay(), components);
  }*/

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
