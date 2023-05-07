package ehealth.group1.backend.helper.dataloaders;

import ehealth.group1.backend.entity.ECGAnalysisSettings;
import ehealth.group1.backend.entity.ECGStateHolderSettings;
import ehealth.group1.backend.entity.GraphicsSettings;
import ehealth.group1.backend.entity.Settings;
import ehealth.group1.backend.repositories.SettingsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
public class DefaultDataLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final int maxDeviation = 5;
    private final int maxDeviationNum = 10;
    private final int iterations_transition = 3;
    private final int iterations_emergency = 5;

    private final SettingsRepository settingsRepository;

    public DefaultDataLoader(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    public void defaultSettings() {
        LOGGER.info("Loading default ECG stateholder- and analysis-settings.");
        settingsRepository.deleteByUserId(0L);

        ECGAnalysisSettings analysisSettings = new ECGAnalysisSettings(0L, maxDeviation, maxDeviationNum);
        ECGStateHolderSettings stateHolderSettings = new ECGStateHolderSettings(0L, iterations_transition, iterations_emergency);
        Settings s = new Settings(0L, stateHolderSettings, analysisSettings);
        settingsRepository.save(s);
    }

    public GraphicsSettings getGraphicsSettings() {
        int canvas_x_size = 800;
        int canvas_y_size = 1600;

        return new GraphicsSettings(canvas_x_size, canvas_y_size);
    }
}
