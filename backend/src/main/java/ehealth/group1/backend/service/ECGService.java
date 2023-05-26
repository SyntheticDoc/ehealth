package ehealth.group1.backend.service;

import ehealth.group1.backend.customfhirstructures.CustomObservation;
import ehealth.group1.backend.entity.*;
import ehealth.group1.backend.enums.ECGSTATE;
import ehealth.group1.backend.exception.DeprecatedException;
import ehealth.group1.backend.exception.ECGStateHolderNotFoundException;
import ehealth.group1.backend.exception.PersistenceException;
import ehealth.group1.backend.exception.UserNotFoundException;
import ehealth.group1.backend.helper.ECGStateHolder;
import ehealth.group1.backend.helper.mock.MockDataProvider;
import ehealth.group1.backend.repositories.*;
import org.hl7.fhir.r5.model.Observation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("EnhancedSwitchMigration")
@Component
public class ECGService {
  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  DataRepository dataRepository;
  ECGAnalysisRepository analysisRepository;
  SettingsRepository settingsRepository;
  UserRepository userRepository;
  DeviceRepository deviceRepository;
  UserService userService;

  private final DataService dataService;
  private final AnalyserService analyserService;
  private final MockDataProvider mockDataProvider;
  private Environment env;

  private ArrayList<String> currentProfiles = new ArrayList<>();

  private HashMap<String, ECGStateHolder> ecgStateHolders = new HashMap<>();

  public ECGService(DataRepository dataRepository, ECGAnalysisRepository analysisRepository, SettingsRepository settingsRepository,
                    DataService dataService, AnalyserService analyserService, UserService userService, UserRepository userRepository,
                    DeviceRepository deviceRepository, MockDataProvider mockDataProvider, Environment env) {
    this.dataRepository = dataRepository;
    this.analysisRepository = analysisRepository;
    this.settingsRepository = settingsRepository;
    this.dataService = dataService;
    this.analyserService = analyserService;
    this.userRepository = userRepository;
    this.deviceRepository = deviceRepository;
    this.userService = userService;
    this.mockDataProvider = mockDataProvider;
    this.env = env;

    currentProfiles.addAll(Arrays.asList(env.getActiveProfiles()));
  }

  @Deprecated
  public List<String> getThing() {
    String[] arr = new String[]{"Accessing dataDao.getThing() is not supported anymore"};
    return Arrays.asList(arr);
  }

  public void processECG(String data) {
    processECGObservation(dataService.getObservation(data));
  }

  public void processECG(CustomObservation data) {
    processECGObservation(dataService.getObservation(data));
  }

  public void processECG_customEsp32(String data) {
    CustomObservation obs = dataService.getObservation_fromCustomEsp32(data);

    if(obs.getDevice().getDisplay().equals("ESP32 custom ecg device")) {
      User user = userRepository.findByNameAndPassword("User Userman3", userService.hashUserPassword("pwd3"));

      if (user.getDevices() == null || user.getDevices().isEmpty()) {
        ECGDevice device = deviceRepository.findECGDeviceByIdentifier(obs.getDeviceID().getValue());
        LOGGER.debug("Device not yet registered to user, registering it to test user \"User Userman3\".");

        if (device == null) {
          throw new PersistenceException("Device data for custom ESP32 could not be found, please register the device first.");
        }

        user.addECGDevice(device);
        userRepository.save(user);
      }
    }

    processECGObservation(obs);
  }

  private void processECGObservation(CustomObservation obs) {
    LOGGER.info("Starting analysis of ecg observation");

    ECGStateHolder ecgStateHolder = checkIfStateHolderPresentElseCreateNew(obs);

    Settings settings = settingsRepository.findByUserId(ecgStateHolder.getSourceUser().getId());

    if(settings == null) {
      LOGGER.debug("Settings for user " + ecgStateHolder.getSourceUser().getName() + " not found, using default settings...");
      settings = settingsRepository.findByUserId(0L);
    }

    ECGAnalysisResult analysisResult = analyserService.analyse(obs, settings);

    LOGGER.debug("AnalyserService() analysisResult: " + analysisResult);

    ecgStateHolder.update(analysisResult, obs);

    LOGGER.info("currentState of stateHolder: " + ecgStateHolder.getCurrent().toString());

    saveECGData(obs, analysisResult, obs.getDeviceID().getValue());

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

  public ECGHealthStatus getLastHealthStatus(RequestDeviceAccess request) throws IllegalAccessException {
    User user = grantDeviceAccess(request);

    // Mock status:
    if(currentProfiles.contains("mock")) {
      if(user.getName().equals("User Userman1") || user.getName().equals("User Userman2")) {
        return mockDataProvider.getForUser(user.getName());
      }
    }

    ECGStateHolder ecgStateHolder = ecgStateHolders.get(request.getDeviceIdentifier());

    if(ecgStateHolder == null) {
      throw new ECGStateHolderNotFoundException("In ECGService.getLastHealthStatus(): Could not find active ECGStateHolder " +
              "for user " + request.getUserName() + ". Make sure that there was data send by an ecg device to the backend " +
              "server recently.");
    }

    ECGHealthStatus result = new ECGHealthStatus();
    result.setAssociatedUserName(user.getName());
    result.setLastAnalysisResult(ecgStateHolder.getCurrentAnalysisResult());

    return result;
  }

  public ECGSTATE getCurrentState() {
    throw new DeprecatedException("The method ECGService.getCurrentState() is deprecated and will be removed in future " +
            "versions of this server.");
  }

  public void abortEmergencyCall(RequestDeviceAccess request) throws IllegalAccessException {
    User user = grantDeviceAccess(request);
    LOGGER.warn("ECG emergency call aborted by user!");
    ECGStateHolder ecgStateHolder = ecgStateHolders.get(request.getDeviceIdentifier());

    if(ecgStateHolder == null) {
      throw new ECGStateHolderNotFoundException("In ECGService.abortEmergencyCall(): Could not find active ECGStateHolder " +
              "for user " + request.getUserName() + ". Make sure that there was data send by an ecg device to the backend " +
              "server recently.");
    }

    ecgStateHolder.abortEmergency();
  }

  public String getData() {
    //return ecgStateHolder.getCurrentObservation().getComponent().get(0).getValueSampledData().getData();
    throw new DeprecatedException("The method ECGService.getData() is deprecated and will be removed in future " +
            "versions of this server.");
  }

  public String getData(String data) {
    throw new DeprecatedException("The method ECGService.getData() is deprecated and will be removed in future " +
            "versions of this server.");
    //return data;
  }

  private void saveECGData(CustomObservation obs, ECGAnalysisResult ecgAnalysisResult, String deviceIdentifier) throws
          IllegalStateException {
    ECGDevice sourceDevice = deviceRepository.findECGDeviceByIdentifier(deviceIdentifier);
    ArrayList<ECGData> datasets = new ArrayList<>();

    for(Observation.ObservationComponentComponent c : obs.getComponent()) {
      ECGDeviceComponent sourceComponent = null;

      for(ECGDeviceComponent sourceComp : sourceDevice.getComponents()) {
        if(sourceComp.getName().equals(c.getCode().getCoding().get(0).getDisplay())) {
          sourceComponent = sourceComp;
          break;
        }
      }

      if(sourceComponent == null) {
        throw new IllegalStateException("Could not get ECGDeviceComponent with the name \"" + c.getCode().getCoding().get(0).getDisplay() +
                "\" from device \"" + sourceDevice.getName() + "\"");
      }

      ECGData data = new ECGData();
      data.setTimestamp(obs.getTimestampAsLocalDateTime());
      data.setComponent(sourceComponent);
      data.setData(c.getValueSampledData().getData());
      data.setEcgAnalysisResult(ecgAnalysisResult);

      datasets.add(data);
    }

    LOGGER.info("Saving datasets...\n" + Arrays.toString(datasets.toArray()) + "\n\n");

    for(ECGData dat : datasets) {
      analysisRepository.save(dat.getEcgAnalysisResult());
      dataRepository.save(dat);
    }

    analysisRepository.flush();
    dataRepository.flush();
  }

  public ECGDataSet getDataSets(RequestDatasets request) throws UserNotFoundException {
    User user = userRepository.findByNameAndPassword(
            request.getUserName(),
            userService.hashUserPassword(request.getPassword()));

    if(user == null) {
      throw new UserNotFoundException("The user with the name " + request.getUserName() + " could not be found. Wrong username " +
              "or password?");
    }

    LocalDateTime start = request.getEnd().minusSeconds(request.getSeconds());

    ECGDataSet result = new ECGDataSet();

    LOGGER.info("Getting all datasets from " + start + " to " + request.getEnd());

    for(ECGData dat : dataRepository.getAllFromTimestampToTimestamp(start, request.getEnd())) {
      result.addToDatasets(dat);
    }

    LOGGER.info("" + result.getDatasets().size() + " datasets found");

    return result;
  }

  private ECGStateHolder checkIfStateHolderPresentElseCreateNew(CustomObservation obs) throws IllegalStateException {
    String deviceIdentifier = obs.getDeviceID().getValue();
    ECGDevice device = deviceRepository.findECGDeviceByIdentifier(deviceIdentifier);

    LOGGER.debug("Trying to check for stateHolder for device " + deviceIdentifier);

    if(device == null) {
      throw new IllegalStateException("In ECGService.checkIfStateHolderPresentElseCreateNew(): Could not find ecg device " +
              "with the device identifier given! Register a device first before sending ecg data!");
    }

    LOGGER.debug("Device found: " + device.getName());

    ECGStateHolder userStateHolder = ecgStateHolders.get(deviceIdentifier);

    if(userStateHolder == null) {
      LOGGER.debug("ecgStateHolder for device not found, creating new stateholder");
      LOGGER.debug("Trying to get user for device...");

      User user = userRepository.findByDevicesContains(device);

      if(user == null) {
        throw new IllegalStateException("In ECGService.checkIfStateHolderPresentElseCreateNew(): Could not find user " +
                "for the current ecg device! Register a device first and connect it with a user before sending ecg data!");
      }

      LOGGER.debug("Device user found: " + user.getName());
      LOGGER.debug("Trying to get settings for user...");

      Settings userSettings = settingsRepository.findByUserId(user.getId());

      if(userSettings == null) {
        LOGGER.debug("Settings not found, using default settings.");
        userSettings = settingsRepository.findByUserId(0L);
      }

      userStateHolder = new ECGStateHolder(userSettings.getEcgStateHolderSettings(), deviceIdentifier, user);

      LOGGER.debug("Successfully created ecgStateHolder for user " + user.getName() + " and device " + device.getName());

      ecgStateHolders.put(deviceIdentifier, userStateHolder);
    }

    return userStateHolder;
  }

  private User grantDeviceAccess(RequestDeviceAccess request) throws IllegalAccessException {
    User user = userRepository.findByNameAndPassword(
            request.getUserName(),
            userService.hashUserPassword(request.getPassword()));

    if(user == null) {
      throw new UserNotFoundException("In ECGService.getLastHealthStatus(): Could not find user " + request.getUserName() + ". " +
              "Please check username and/or password.");
    }

    boolean deviceBelongsToUser = false;

    for(ECGDevice d : user.getDevices()) {
      if (d.getIdentifier().equals(request.getDeviceIdentifier())) {
        deviceBelongsToUser = true;
        break;
      }
    }

    if(!deviceBelongsToUser) {
      throw new IllegalAccessException("The device requested is not bound to the user " + request.getUserName() + "! Possible " +
              "illegal data access. Please check that the device is already connected to this user.");
    }

    return user;
  }
}
