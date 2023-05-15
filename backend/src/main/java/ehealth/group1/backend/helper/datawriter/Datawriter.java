package ehealth.group1.backend.helper.datawriter;

import ehealth.group1.backend.entity.Settings;
import ehealth.group1.backend.helper.ErrorHandler;
import ehealth.group1.backend.repositories.SettingsRepository;
import org.hl7.fhir.r5.model.Observation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SuppressWarnings("FieldCanBeLocal")
@Component
public class Datawriter {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    SettingsRepository settingsRepository;
    Settings settings;
    ErrorHandler errorHandler;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm:ss:SSS");
    private final String baseFilename = ".\\backend\\src\\main\\resources\\data.out\\ReceivedData";
    private final String fileExtension = ".txt";

    public Datawriter(SettingsRepository settingsRepository, ErrorHandler errorHandler) {
        this.settingsRepository = settingsRepository;
        this.errorHandler = errorHandler;
        settings = settingsRepository.findByUserId(0L);
    }

    public void writeData(Observation obs) {
        String filename = getNextFilename();
        LOGGER.info("Writing ecg data to " + filename + "...");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(dtf.format(LocalDateTime.now()));
            writer.newLine();

            for(int i = 0; i < obs.getComponent().size(); i++) {
                Observation.ObservationComponentComponent c = obs.getComponent().get(i);
                String data = c.getValueSampledData().getData();
                writer.write(data);

                if(i < (obs.getComponent().size() - 1)) {
                    writer.newLine();
                }
            }

            LOGGER.info("Data successfully written!");
        } catch(IOException e) {
            errorHandler.handleCustomException("Datawriter.writeData()", "Unable to write file " + filename, e);
        }
    }

    private String getNextFilename() {
        int lastFilenum = settings.getDataWriter_lastFileNum();

        String result = baseFilename + lastFilenum + fileExtension;

        lastFilenum++;
        settings.setDataWriter_lastFileNum(lastFilenum);

        return result;
    }
}
