package ehealth.group1.backend.helper;

import ehealth.group1.backend.helper.dataloaders.DefaultDataLoader;
import ehealth.group1.backend.repositories.SettingsRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
public class Initializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    DefaultDataLoader dataLoader;
    SettingsRepository settingsRepository;

    public Initializer(DefaultDataLoader dataLoader, SettingsRepository settingsRepository) {
        this.dataLoader = dataLoader;
        this.settingsRepository = settingsRepository;
    }

    @PostConstruct
    public void init() {
        LOGGER.info("Performing initialization routines...");

        if(settingsRepository.findByUserId(0L) == null) {
            LOGGER.warn("No default ecg settings found!");
            dataLoader.defaultSettings();
        }
    }
}
