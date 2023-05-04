package ehealth.group1.backend.rest;

import ehealth.group1.backend.entity.ConnectDeviceData;
import ehealth.group1.backend.entity.ECGDevice;
import ehealth.group1.backend.entity.FrontendDevice;
import ehealth.group1.backend.entity.User;
import ehealth.group1.backend.service.ConnectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(path = "/connect")
public class ConnectionEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    ConnectionService connectionService;

    public ConnectionEndpoint(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    /**
     * Endpoint to register a ECGDevice and save all important device data in the database. Generates an identifier-string
     * to uniquely identify the device which has to be attached to all future incoming data from the registered device to
     * know to which device the data belongs.
     *
     * @param ecgDevice The device data
     * @return A unique device identifier as String
     */
    @PostMapping("/registerECGDevice")
    @ResponseStatus(HttpStatus.CREATED)
    public String registerECGDevice(@RequestBody ECGDevice ecgDevice) {
        return connectionService.registerECGDevice(ecgDevice);
    }

    /**
     * Endpoint to register a FrontendDevice and save all important device data in the database. Generates an identifier-string
     * to uniquely identify the device which has to be attached to all future incoming requests from the registered device to
     * know to which device the requests belongs.
     *
     * @param frontendDevice The device data
     * @return A unique device identifier as String
     */
    @PostMapping("/registerFrontendDevice")
    @ResponseStatus(HttpStatus.CREATED)
    public String registerECGDevice(@RequestBody FrontendDevice frontendDevice) {
        return connectionService.registerFrontendDevice(frontendDevice);
    }

    @PostMapping("/registerUser")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerUser(@RequestBody User user) {
        connectionService.registerUser(user);
    }

    @PostMapping("/connectECGDeviceToUser")
    @ResponseStatus(HttpStatus.CREATED)
    public void connectECGDeviceToUser(@RequestBody ConnectDeviceData data) {
        connectionService.connectECGDeviceToUser(data);
    }
}
