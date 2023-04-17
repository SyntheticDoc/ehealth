package ehealth.group1.backend.rest;

import ehealth.group1.backend.service.ECGService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

@RestController
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

  @PostMapping("/data")
  public String getData(@RequestBody String data){
    return ecgService.getData(data);
  }
}
