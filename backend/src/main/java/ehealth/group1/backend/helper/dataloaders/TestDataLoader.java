package ehealth.group1.backend.helper.dataloaders;

import ehealth.group1.backend.entity.User;
import ehealth.group1.backend.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
public class TestDataLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private DataRepository dataRepository;
    private DeviceRepository deviceRepository;
    private ECGDataRepository ecgDataRepository;
    private UserRepository userRepository;

    public TestDataLoader(DataRepository dataRepository, DeviceRepository deviceRepository, ECGDataRepository ecgDataRepository, UserRepository userRepository) {
        this.dataRepository = dataRepository;
        this.deviceRepository = deviceRepository;
        this.ecgDataRepository = ecgDataRepository;
        this.userRepository = userRepository;
    }

    public void exec() {
        LOGGER.warn("TestDataLoader called, loading test data into database...");
        LOGGER.warn("Executing the TestDataLoader will delete all prior data in the database!");

        dataRepository.deleteAll();
        ecgDataRepository.deleteAll();
        userRepository.deleteAll();
        deviceRepository.deleteAll();

        User u = userRepository.save(new User("TestUser", "Example Street 54/Stiege 300/top 0, 1010 Wien", 6500123456L, true, "VeryUnsafePWD"));

        LOGGER.debug("User test: " + userRepository.findById(u.getId()));
        LOGGER.debug("User test, number of users in database: " + userRepository.findAll().size());

        LOGGER.info("All test data successfully inserted.");
    }
}
