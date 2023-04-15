package ehealth.group1.backend.rest;

import ehealth.group1.backend.service.ECGService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DataEndpoint {
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
