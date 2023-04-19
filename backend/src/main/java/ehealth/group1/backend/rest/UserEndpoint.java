package ehealth.group1.backend.rest;

import ehealth.group1.backend.service.ECGService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(path = "/user")
public class UserEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    ECGService ecgService;

    public UserEndpoint(ECGService ecgService){
        this.ecgService = ecgService;
    }

    @GetMapping("/")
    public int returnHealthStatus(){
        return 1;
    }

    @PostMapping("/getRecentEcg")
    public String getRecentEcgData(@RequestBody String data){
        LOGGER.info("/data called?");
        return ecgService.getData(data);
    }

}
