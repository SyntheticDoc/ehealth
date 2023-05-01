package ehealth.group1.backend.helper;

import ehealth.group1.backend.entity.ECGAnalysisSettings;
import ehealth.group1.backend.entity.ECGStateHolderSettings;
import ehealth.group1.backend.entity.Settings;
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
    private SettingsRepository settingsRepository;
    private UserRepository userRepository;

    private final int maxDeviation = 5;
    private final int maxDeviationNum = 10;
    private final int iterations_transition = 3;
    private final int iterations_emergency = 5;

    public TestDataLoader(DataRepository dataRepository, DeviceRepository deviceRepository, ECGDataRepository ecgDataRepository, SettingsRepository settingsRepository, UserRepository userRepository) {
        this.dataRepository = dataRepository;
        this.deviceRepository = deviceRepository;
        this.ecgDataRepository = ecgDataRepository;
        this.settingsRepository = settingsRepository;
        this.userRepository = userRepository;
    }

    public void exec() {
        LOGGER.warn("TestDataLoader called, loading test data into database...");
        LOGGER.warn("Executing the TestDataLoader will delete all prior data in the database!");

        dataRepository.deleteAll();
        deviceRepository.deleteAll();
        ecgDataRepository.deleteAll();
        settingsRepository.deleteAll();
        userRepository.deleteAll();

        User u = userRepository.save(new User("TestUser", "Example Street 54/Stiege 300/top 0, 1010 Wien", 6500123456L, true, "VeryUnsafePWD"));

        ECGAnalysisSettings analysisSettings = new ECGAnalysisSettings(u.getId(), maxDeviation, maxDeviationNum);
        ECGStateHolderSettings stateHolderSettings = new ECGStateHolderSettings(u.getId(), iterations_transition, iterations_emergency);
        Settings s = new Settings(u.getId(), stateHolderSettings, analysisSettings);
        settingsRepository.save(s);

        LOGGER.debug("User test: " + userRepository.findById(u.getId()));
        LOGGER.debug("User test, number of users in database: " + userRepository.findAll().size());

        LOGGER.info("All test data successfully inserted.");
    }
}
