package ehealth.group1.backend.helper.dataloaders;

import ehealth.group1.backend.entity.ECGAnalysisSettings;
import ehealth.group1.backend.entity.ECGStateHolderSettings;
import ehealth.group1.backend.entity.GraphicsSettings;
import ehealth.group1.backend.entity.Settings;
import ehealth.group1.backend.repositories.SettingsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.lang.invoke.MethodHandles;

@Component
public class DefaultDataLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final double maxDeviation = 0.2;
    private final double maxDeviationNum = 5.0;
    private final int iterations_transition = 3;
    private final int iterations_emergency = 5;

    private final SettingsRepository settingsRepository;

    public DefaultDataLoader(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    public void defaultSettings() {
        LOGGER.info("Loading default ECG stateholder- and analysis-settings.");
        if(settingsRepository.findByUserId(0L) != null) {
            settingsRepository.deleteByUserId(0L);
        }

        ECGAnalysisSettings analysisSettings = new ECGAnalysisSettings(0L, maxDeviation, maxDeviationNum);
        ECGStateHolderSettings stateHolderSettings = new ECGStateHolderSettings(0L, iterations_transition, iterations_emergency);
        Settings s = new Settings(0L, stateHolderSettings, analysisSettings);
        settingsRepository.save(s);
    }

    public GraphicsSettings getGraphicsSettings() {
        int titleBarSize = 30;
        int canvas_x_size = 1600;
        int canvas_y_size = 800;
        double lineThickness_dividerLines = 0.005;
        double lineThickness_ecgGraph = 0.005;
        Color background = Color.WHITE;
        Color titleBar = Color.LIGHT_GRAY;
        Color base = Color.BLACK;
        Color ecgGraph = Color.RED;
        Color text = Color.BLACK;
        Font leadName = new Font("Arial", Font.BOLD, 18);
        Font timestamp = new Font("Arial", Font.BOLD, 14);

        boolean useDoubleBuffering = true;

        GraphicsSettings settings = new GraphicsSettings();

        settings.setTitleBarSize(titleBarSize);
        settings.setCanvas_x_size(canvas_x_size);
        settings.setCanvas_y_size(canvas_y_size);
        settings.setLineThickness_dividerLines(lineThickness_dividerLines);
        settings.setLineThickness_ecgGraph(lineThickness_ecgGraph);
        settings.setBackground(background);
        settings.setTitleBar(titleBar);
        settings.setBase(base);
        settings.setEcgGraph(ecgGraph);
        settings.setText(text);
        settings.setFont_leadName(leadName);
        settings.setFont_timestamp(timestamp);
        settings.setUseDoubleBuffering(useDoubleBuffering);

        return settings;
    }
}
