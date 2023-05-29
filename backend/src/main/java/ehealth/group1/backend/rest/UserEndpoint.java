package ehealth.group1.backend.rest;

import ehealth.group1.backend.entity.User;
import ehealth.group1.backend.entity.UserUpdate;
import ehealth.group1.backend.helper.ErrorHandler;
import ehealth.group1.backend.service.ECGService;
import ehealth.group1.backend.service.MessagingService;
import ehealth.group1.backend.service.UserService;
import jakarta.persistence.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    ErrorHandler errorHandler;

    public UserEndpoint(ECGService ecgService,MessagingService msgService, UserService userService, ErrorHandler errorHandler){
        this.ecgService = ecgService;
        this.msgService = msgService;
        this.userService = userService;
        this.errorHandler = errorHandler;
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
            errorHandler.handleCustomException("msgService.sendSMS()", "Could not send message", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not send message: " + e.getMessage(), e);
        }
        return "Alrighty";
    }

    @GetMapping("/get-user")
    public User getUser(@RequestParam String name, @RequestParam String password){
        try {
            return userService.getUser(name, password);
        } catch (Exception e) {
            errorHandler.handleCustomException("userService.getUser()", "Could not get user", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not get user: " + e.getMessage(), e);
        }
    }

    @PostMapping("/post-user")
    public User postUser(@RequestBody User user){
        try {
            return userService.postUser(user);
        } catch(PersistenceException e) {
            errorHandler.handleCustomException("userService.postUser()", "Could not create new user", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not create new user: " + e.getMessage(), e);
        }
    }

    @PostMapping("/update-user")
    public User updateUser(@RequestBody UserUpdate userUpdate){
        try {
            return userService.updateUser(userUpdate);
        } catch(PersistenceException e) {
            errorHandler.handleCustomException("userService.updateUser()", "Could not update user", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not update user: " + e.getMessage(), e);
        }
    }
}
