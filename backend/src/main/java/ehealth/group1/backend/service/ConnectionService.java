package ehealth.group1.backend.service;

import ehealth.group1.backend.entity.*;
import ehealth.group1.backend.generators.IDStringGenerator;
import ehealth.group1.backend.repositories.DataRepository;
import ehealth.group1.backend.repositories.DeviceRepository;
import ehealth.group1.backend.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
public class ConnectionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    DataRepository dataRepository;
    DeviceRepository deviceRepository;
    UserRepository userRepository;

    public ConnectionService(DataRepository dataRepository, DeviceRepository deviceRepository, UserRepository userRepository) {
        this.dataRepository = dataRepository;
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
    }

    public ECGDevice registerECGDevice(ECGDevice ecgDevice) {
        ecgDevice.setIdentifier(IDStringGenerator.getNewIDString());

        for (ECGDeviceComponent c : ecgDevice.getComponents()) {
            c.setIdentifier(IDStringGenerator.getNewIDString());
        }

        deviceRepository.save(ecgDevice);

        System.out.println("\n\nMock registering: " + ecgDevice + "\n");
        return ecgDevice;
    }

    public FrontendDevice registerFrontendDevice(FrontendDevice frontendDevice) {
        frontendDevice.setIdentifier(IDStringGenerator.getNewIDString());

        System.out.println("\n\nMock registering: " + frontendDevice + "\n");
        return frontendDevice;
    }

    public void registerUser(User user) {
        userRepository.save(user);
    }

    public void connectECGDeviceToUser(ConnectDeviceData data) {
        ECGDevice device = deviceRepository.findECGDeviceByNameAndPin(data.getRegDeviceName(), data.getRegDevicePin());
        LOGGER.warn("ConnectionService.connectECGDeviceToUser(): " + device);
    }
}
