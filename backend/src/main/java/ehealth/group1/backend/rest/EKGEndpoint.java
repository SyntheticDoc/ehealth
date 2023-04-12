package ehealth.group1.backend.rest;

import ehealth.group1.backend.service.EKGService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EKGEndpoint {
  EKGService ekgService;
  public EKGEndpoint(EKGService ekgService){
    this.ekgService = ekgService;
  }

  @GetMapping("/")
  public List<String> home(){
    return ekgService.getThing();
  }
}
