package ehealth.group1.backend.rest;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ehealth.group1.backend.config.FhirHapiDeserializer;
import ehealth.group1.backend.customfhirstructures.CustomObservation;
import ehealth.group1.backend.entity.ECGHealthStatus;
import ehealth.group1.backend.entity.User;
import ehealth.group1.backend.repositories.DeviceRepository;
import ehealth.group1.backend.service.ECGService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

  // Method for letting spring boot deserialize the json immediately into a custom Observation, using a custom
  // FhirHapiDeserializer to replace the default jackson deserializer
  @RequestMapping(value="/receive", consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
  //@JsonDeserialize(using = FhirHapiDeserializer.class)
  @ResponseStatus(HttpStatus.OK)
  public void receiveObservation(@RequestBody CustomObservation obs) {
    LOGGER.info("Received ecg data from client (obs)");
    ecgService.processECG(obs);
  }

  // Method for processing incoming data missing a "Content-Type: application/json"-header or otherwise not conforming
  // to the json-format expected by the custom FhirHapiDeserializer
  @PostMapping("/receive")
  @ResponseStatus(HttpStatus.OK)
  public void receiveJson(@RequestBody String data) {
    LOGGER.info("Received ecg data from client");
    ecgService.processECG(data);
  }

  // Method for processing incoming reduced dataset explicitly from our custom esp32-device
  @PostMapping("/receive/esp32_custom")
  @ResponseStatus(HttpStatus.OK)
  public void receiveJson_fromCustomEsp32(@RequestBody String data) {
    LOGGER.info("Received ecg data from custom ecp 32");
    ecgService.processECG_customEsp32(data);
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
  public ECGHealthStatus reportLastHealthStatus(@RequestBody User user) {
    return ecgService.getLastHealthStatus(user);
  }
}
