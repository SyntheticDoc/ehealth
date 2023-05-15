package ehealth.group1.backend.service;

import ehealth.group1.backend.entity.*;
import ehealth.group1.backend.exception.UserAlreadyExistsException;
import ehealth.group1.backend.generators.IDStringGenerator;
import ehealth.group1.backend.helper.argon2crypto.Argon2PasswordEncoderWithParams;
import ehealth.group1.backend.repositories.DataRepository;
import ehealth.group1.backend.repositories.DeviceRepository;
import ehealth.group1.backend.repositories.UserRepository;
import jakarta.persistence.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.text.ParseException;

@Component
public class ConnectionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    DataRepository dataRepository;
    DeviceRepository deviceRepository;
    UserRepository userRepository;
    private Argon2PasswordEncoderWithParams passwordEncoder;

    public ConnectionService(DataRepository dataRepository, DeviceRepository deviceRepository, UserRepository userRepository,
                             Argon2PasswordEncoderWithParams passwordEncoder) {
        this.dataRepository = dataRepository;
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers an ECGDevice in the database and generates a unique identifier for it.
     *
     * @param ecgDevice The device to register in the database.
     * @return A JSON string containing the generated identifier for the device.
     */
    public String registerECGDevice(ECGDevice ecgDevice) {
        ecgDevice.setIdentifier(IDStringGenerator.getNewIDString());

        for (ECGDeviceComponent c : ecgDevice.getComponents()) {
            c.setIdentifier(IDStringGenerator.getNewIDString());
        }

        deviceRepository.save(ecgDevice);

        return "{\"identifier\" : \"" + ecgDevice.getIdentifier() + "\"}";
    }

    /**
     * Registers a FrontendDevice in the database and generates a unique identifier for it.
     *
     * @param frontendDevice The device to register in the database.
     * @return A JSON string containing the generated identifier for the device.
     */
    public String registerFrontendDevice(FrontendDevice frontendDevice) {
        frontendDevice.setIdentifier(IDStringGenerator.getNewIDString());

        System.out.println("\n\nMock registering: " + frontendDevice + "\n");
        return "{\"identifier\" : \"" + frontendDevice.getIdentifier() + "\"}";
    }

    /**
     * Registers a user in the database. Checks if the user already exists and throws an exception in this case.
     *
     * @param user The user to be registered.
     */
    public void registerUser(User user) throws UserAlreadyExistsException {
        User user2 = userRepository.findByName(user.getName());

        if(user.getPhone().equals(user2.getPhone())) {
            throw new UserAlreadyExistsException("A user with the name " + user.getName() + " and the same phone number " +
                    "already exists! No new user was created!");
        }

        if(user.getAddress().equals(user2.getAddress())) {
            throw new UserAlreadyExistsException("A user with the name " + user.getName() + " and the same address " +
                    "already exists! No new user was created!");
        }

        // TODO: Hash password
        // Build Argon2-hash from users password
        // user.setPassword(passwordEncoder.encode(user.getPassword(), null, null));

        userRepository.save(user);
    }

    /**
     * Connects an ECGDevice matching the pin provided in data to the user provided in data
     *
     * @param data ConnectDeviceData containing the pin of the device and the user data of the user to connect
     * @return A JSON string containing the unique device identifier of the connected ECGDevice
     * @throws PersistenceException If any data could not be retrieved from the database
     */
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
