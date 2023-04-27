package ehealth.group1.backend.rest;

import ehealth.group1.backend.entity.ECGDevice;
import ehealth.group1.backend.entity.ECGDeviceComponent;
import ehealth.group1.backend.entity.FrontendDevice;
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

    @PostMapping("/registerECGDevice")
    @ResponseStatus(HttpStatus.OK)
    public ECGDevice registerECGDevice(@RequestBody ECGDevice ecgDevice) {
        return connectionService.registerECGDevice(ecgDevice);
    }

    @PostMapping("/registerFrontendDevice")
    @ResponseStatus(HttpStatus.OK)
    public FrontendDevice registerECGDevice(@RequestBody FrontendDevice frontendDevice) {
        return connectionService.registerFrontendDevice(frontendDevice);
    }
}
