package ehealth.group1.backend.service;

import ehealth.group1.backend.entity.*;
import ehealth.group1.backend.generators.IDStringGenerator;
import ehealth.group1.backend.repositories.DataRepository;
import ehealth.group1.backend.repositories.DeviceRepository;
import ehealth.group1.backend.repositories.UserRepository;
import jakarta.persistence.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.text.ParseException;

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

    public String registerECGDevice(ECGDevice ecgDevice) {
        ecgDevice.setIdentifier(IDStringGenerator.getNewIDString());

        for (ECGDeviceComponent c : ecgDevice.getComponents()) {
            c.setIdentifier(IDStringGenerator.getNewIDString());
        }

        deviceRepository.save(ecgDevice);

        return "{\"identifier\" : \"" + ecgDevice.getIdentifier() + "\"}";
    }

    public String registerFrontendDevice(FrontendDevice frontendDevice) {
        frontendDevice.setIdentifier(IDStringGenerator.getNewIDString());

        System.out.println("\n\nMock registering: " + frontendDevice + "\n");
        return "{\"identifier\" : \"" + frontendDevice.getIdentifier() + "\"}";
    }

    public void registerUser(User user) {
        userRepository.save(user);
    }

    public String connectECGDeviceToUser(ConnectDeviceData data) throws PersistenceException {
        LOGGER.info("Trying to connect ECGDevice to user...");

        ECGDevice device = deviceRepository.findECGDeviceByNameAndPin(data.getRegDeviceName(), data.getRegDevicePin());
        User user = userRepository.findByNameAndPassword(data.getUserName(), data.getPassword());

        if(device == null) {
            throw new PersistenceException("Can't find ECGDevice \"" + data.getRegDeviceName() + "\", check name and pin!");
        }

        if(user == null) {
            throw new PersistenceException("Can't find User \"" + data.getUserName() + "\", check user name and password!");
        }

        if(user.getDevices().contains(device)) {
            throw new PersistenceException("Can't add ECGDevice to user, ECGDevice is already registered for this user!");
        }

        user.addECGDevice(device);
        userRepository.save(user);

        LOGGER.info("Successfully added ECGDevice to user.");

        // TODO: Remove
        LOGGER.info("Saved user: " + user);

        return "{\"identifier\" : \"" + device.getIdentifier() + "\"}";
    }
}
