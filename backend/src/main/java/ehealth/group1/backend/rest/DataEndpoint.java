package ehealth.group1.backend.rest;

import ehealth.group1.backend.service.DataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DataEndpoint {
  DataService ekgService;
  public DataEndpoint(DataService ekgService){
    this.ekgService = ekgService;
  }

  @GetMapping("/")
  public int returnHealthStatus(){
    return 1;
  }

  @PostMapping("/data")
  public String getData(@RequestBody String data){
    return ekgService.getData(data);
  }
}
