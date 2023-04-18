package ehealth.group1.backend.rest;

import ehealth.group1.backend.jely.JELYmaster.de.fau.mad.jely.Heartbeat;
import ehealth.group1.backend.jely.JELYmaster.de.fau.mad.jely.LeadConfiguration;
import ehealth.group1.backend.jely.JELYmaster.de.fau.mad.jely.QrsComplex;
import ehealth.group1.backend.jely.JELYmaster.de.fau.mad.jely.detectors.HeartbeatDetector;
import ehealth.group1.backend.jely.JELYmaster.de.fau.mad.jely.Ecg;
import ehealth.group1.backend.jely.JELYmaster.de.fau.mad.jely.io.FileLoader;
import ehealth.group1.backend.service.ECGService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;

@RestController
@RequestMapping(path = "/data")
public class DataEndpoint {
  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  ECGService ecgService;

  public DataEndpoint(ECGService ecgService){
    this.ecgService = ecgService;
  }

  @GetMapping("/")
  public int returnHealthStatus(){
    return 1;
  }

  @PostMapping("/test")
  public String getData(@RequestBody String data){
    LOGGER.info("/data called?");
    return ecgService.getData(data);
  }

  @PostMapping("/receive")
  @ResponseStatus(HttpStatus.OK)
  public void receiveData(@RequestBody String data) {
    LOGGER.info("Received ecg data from client");
    LOGGER.debug("\n" + "Data:\n" + data + "\n");
    ecgService.processECG(data);
  }

  @GetMapping("/jelytest")
  @ResponseStatus(HttpStatus.OK)
  public void jelyTest() {
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
