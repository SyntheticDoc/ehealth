package ehealth.group1.backend.rest;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ehealth.group1.backend.entity.ConnectDeviceData;
import ehealth.group1.backend.entity.ECGDevice;
import ehealth.group1.backend.entity.FrontendDevice;
import ehealth.group1.backend.entity.User;
import ehealth.group1.backend.helper.ErrorHandler;
import ehealth.group1.backend.service.ConnectionService;
import jakarta.persistence.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(path = "/connect")
public class ConnectionEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    ConnectionService connectionService;
    ErrorHandler errorHandler;

    public ConnectionEndpoint(ConnectionService connectionService, ErrorHandler errorHandler) {
        this.connectionService = connectionService;
        this.errorHandler = errorHandler;
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
    @JsonDeserialize(using = JsonDeserializer.class)
    @ResponseStatus(HttpStatus.CREATED)
    public String registerECGDevice(@RequestBody ECGDevice ecgDevice) {
        LOGGER.info("ECGDevice: " + ecgDevice);
        try {
            return connectionService.registerECGDevice(ecgDevice);
        } catch(Exception e) {
            errorHandler.handleCustomException("connectionService.registerECGDevice()", "Could not register device", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not register device: " + e.getMessage(), e);
        }
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
    public String registerFrontendDevice(@RequestBody FrontendDevice frontendDevice) {
        try {
            return connectionService.registerFrontendDevice(frontendDevice);
        } catch(Exception e) {
            errorHandler.handleCustomException("connectionService.registerFrontendDevice()", "Could not register device", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not register device: " + e.getMessage(), e);
        }
    }

    /**
     * Connects an ECGDevice to a specific user.
     *
     * @param data Provides data about the user and a pin to identify the ECGDevice to connect to.
     * @return Returns a JSON containing the generated identifier for the connected ECGDevice.
     */
    @PostMapping("/connectECGDeviceToUser")
    @ResponseStatus(HttpStatus.CREATED)
    public String connectECGDeviceToUser(@RequestBody ConnectDeviceData data) {
        try {
            return connectionService.connectECGDeviceToUser(data);
        } catch(PersistenceException e) {
            errorHandler.handleCustomException("connectionService.connectECGDeviceToUser()", "Could not connect ECGDevice to user", e);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Could not connect ECGDevice to user: " + e.getMessage(), e);
        }
    }
}
