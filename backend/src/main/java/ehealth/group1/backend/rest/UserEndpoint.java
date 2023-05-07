package ehealth.group1.backend.rest;

import ehealth.group1.backend.entity.User;
import ehealth.group1.backend.service.ECGService;
import ehealth.group1.backend.service.MessagingService;
import ehealth.group1.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(path = "/user")
public class UserEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    ECGService ecgService;
    MessagingService msgService;
    UserService userService;

    public UserEndpoint(ECGService ecgService,MessagingService msgService, UserService userService){
        this.ecgService = ecgService;
        this.msgService = msgService;
        this.userService = userService;
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
            msgService.sendSMS(recipient,message);
        } catch (IOException e) {
            e.printStackTrace();
            return "OOOpsie";
        }
        return "Alrighty";
    }

    @GetMapping("/get-user")
    public User getUser(@RequestParam Long id){
        List<User> userList = null;
        try {
            return userService.getUser(id);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping("/post-user")
    public User postUser(@RequestParam String name, @RequestParam String address, @RequestParam Long phone,@RequestParam boolean emergency,@RequestParam String password){
        try{
            return userService.postUser(new User(name, address, phone, emergency, password));
        }catch(Error e){
            return null;

        }
    }


    //update USer

}
