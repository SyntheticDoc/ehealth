package ehealth.group1.backend.rest;

import ehealth.group1.backend.customfhirstructures.CustomObservation;
import ehealth.group1.backend.entity.*;
import ehealth.group1.backend.helper.ErrorHandler;
import ehealth.group1.backend.entity.ECGHealthStatus;
import ehealth.group1.backend.jely.JELYmaster.de.fau.mad.jely.*;
import ehealth.group1.backend.jely.JELYmaster.de.fau.mad.jely.detectors.HeartbeatDetector;
import ehealth.group1.backend.jely.JELYmaster.de.fau.mad.jely.io.FileLoader;
import ehealth.group1.backend.service.ECGService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;

@RestController
@RequestMapping(path = "/data")
public class DataEndpoint {
  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  ECGService ecgService;
  ErrorHandler errorHandler;

  public DataEndpoint(ECGService ecgService, ErrorHandler errorHandler){
    this.ecgService = ecgService;
    this.errorHandler = errorHandler;
  }

  // Method for letting spring boot deserialize the json immediately into a custom Observation, using a custom
  // FhirHapiDeserializer to replace the default jackson deserializer
  @RequestMapping(value="/receive", consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
  //@JsonDeserialize(using = FhirHapiDeserializer.class)
  @ResponseStatus(HttpStatus.OK)
  public void receiveObservation(@RequestBody CustomObservation obs) {
    LOGGER.info("Received ecg data from client (obs)");
    try {
      ecgService.processECG(obs);
    } catch(Exception e) {
      errorHandler.handleCustomException("ecgService.processECG_customEsp32()", "Could not process data", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not process data: " + e.getMessage(), e);
    }
  }

  // Method for processing incoming data missing a "Content-Type: application/json"-header or otherwise not conforming
  // to the json-format expected by the custom FhirHapiDeserializer
  @PostMapping("/receive")
  @ResponseStatus(HttpStatus.OK)
  public void receiveJson(@RequestBody String data) {
    LOGGER.info("Received ecg data from client");
    try {
      ecgService.processECG(data);
    } catch(Exception e) {
      errorHandler.handleCustomException("ecgService.processECG()", "Could not process data", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not process data: " + e.getMessage(), e);
    }
  }

  // Method for processing incoming reduced dataset explicitly from our custom esp32-device
  @PostMapping("/receive/esp32_custom")
  @ResponseStatus(HttpStatus.OK)
  public void receiveJson_fromCustomEsp32(@RequestBody String data) {
    LOGGER.info("Received ecg data from custom ecp 32");
    try {
      ecgService.processECG_customEsp32(data);
    } catch(Exception e) {
      errorHandler.handleCustomException("ecgService.processECG_customEsp32()", "Could not process data", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not process data: " + e.getMessage(), e);
    }
  }

  // For testing purposes, reflects the body of the incoming post message back to the sender
  @PostMapping("/reflect")
  @ResponseStatus(HttpStatus.OK)
  public String reflect(@RequestBody String data) {
    LOGGER.info("Reflected data:\n\n" + data + "\n");
    return data;
  }

  @PostMapping("/lastHealthStatus")
  @ResponseStatus(HttpStatus.OK)
  public ECGHealthStatus reportLastHealthStatus(@RequestBody RequestDeviceAccess request) {
    try {
      return ecgService.getLastHealthStatus(request);
    } catch(Exception e) {
      errorHandler.handleCustomException("ecgService.getLastHealthStatus()", "Could not process request", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not process request: " + e.getMessage(), e);
    }
  }

  @PostMapping("/getDataset")
  @ResponseStatus(HttpStatus.OK)
  public ECGDataSet getDatasets(@RequestBody RequestDatasets request) {
    try {
      return ecgService.getDataSets(request);

    } catch(Exception e) {
      errorHandler.handleCustomException("ecgService.getDatasets()", "Could not process request", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not process request: " + e.getMessage(), e);
    }
  }

  @PostMapping("/stopEmergency")
  @ResponseStatus(HttpStatus.OK)
  public void stopEmergency(@RequestBody RequestDeviceAccess request) {
    try {
      ecgService.abortEmergencyCall(request);
      LOGGER.info("Stop emergency worked!");
    } catch(Exception e) {
      errorHandler.handleCustomException("ecgService.abortEmergencyCall()", "Could not process request", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not process request: " + e.getMessage(), e);
    }
  }

  @GetMapping("/jelytest")
  @ResponseStatus(HttpStatus.OK)
  public void jelyTest() {
    Ecglib.setDebugMode(true);
    LOGGER.info("EcgLib.isDebugMode? " + Ecglib.isDebugMode());
    Ecg ecgFile = FileLoader.loadKnownEcgFile("src/main/java/ehealth/group1/backend/jely/testfiles/jelyecg4.csv",
            LeadConfiguration.SINGLE_UNKNOWN_LEAD, 125);
    LOGGER.info("\n\necgFile: " + ecgFile.toString() + "\n\n");
    HeartbeatDetector detector = new HeartbeatDetector(ecgFile, (HeartbeatDetector.HeartbeatDetectionListener) null);
    ArrayList<Heartbeat> beatList = detector.findHeartbeats();

    LOGGER.info("\n\nBeatlist: " + Arrays.toString(beatList.toArray()) + "\n\n");

    for(Heartbeat h : beatList) {
      QrsComplex qrs = h.getQrs();
      int rPeak = qrs.getRPosition();
      LOGGER.info("\n\nHeartbeat: " + h.toString() + "\nQRS: " + qrs.toString() + "\nR-peak: " + rPeak + "\n\n");
    }
  }
}
