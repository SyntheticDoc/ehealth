package ehealth.group1.backend.rest;

import ehealth.group1.backend.service.ECGService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(path = "/data")
public class DataEndpoint {
  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  ECGService ecgService;

  public DataEndpoint(ECGService ecgService){
    this.ecgService = ecgService;
  }

  @PostMapping("/receive")
  @ResponseStatus(HttpStatus.OK)
  public void receiveData(@RequestBody String data) {
    LOGGER.info("Received ecg data from client");
    LOGGER.debug("\n" + "Data:\n" + data + "\n");
    ecgService.processECG(data);
  }

  /*@PostMapping("/entityTest")
  @ResponseStatus(HttpStatus.OK)
  public String testEntityConversion(@RequestBody String data) {
    LOGGER.info("Received ecg data from client");
    return ecgService.convertToEntity(data).getJSONRepresentation();
  }*/
}
