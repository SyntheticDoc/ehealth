package ehealth.group1.backend.rest;

import ehealth.group1.backend.service.ECGService;
import ehealth.group1.backend.service.MessagingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(path = "/user")
public class UserEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    ECGService ecgService;
    MessagingService msgService;

    public UserEndpoint(ECGService ecgService,MessagingService msgService){
        this.ecgService = ecgService;
        this.msgService = msgService;
    }

    @PostMapping("/test")
    @ResponseStatus(HttpStatus.OK)
    public String testConnection(@RequestBody String data) {
        return "Connection test successful. Your data: " + data;
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

    @GetMapping("/sendsms")
    public String sendSMS(@RequestParam String recipient,@RequestParam String message){
        try {
            msgService.sendSMS("+4369913242451","Sikerim");
        } catch (IOException e) {
            e.printStackTrace();
            return "OOOpsie";
        }
        return "Alrighty";
    }

    //get User
    //post User
    //update USer

}
