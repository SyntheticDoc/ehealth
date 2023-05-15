package ehealth.group1.backend.helper.datawriter;

import ehealth.group1.backend.entity.Settings;
import ehealth.group1.backend.helper.ErrorHandler;
import ehealth.group1.backend.helper.PathFinder;
import ehealth.group1.backend.repositories.SettingsRepository;
import org.hl7.fhir.r5.model.Observation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("FieldCanBeLocal")
@Component
public class Datawriter {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    SettingsRepository settingsRepository;
    Settings settings;
    ErrorHandler errorHandler;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm:ss:SSS");
    private final String baseFilename;
    private final String fileExtension = ".txt";

    public Datawriter(SettingsRepository settingsRepository, ErrorHandler errorHandler) {
        this.settingsRepository = settingsRepository;
        this.errorHandler = errorHandler;
        settings = settingsRepository.findByUserId(0L);

        // Load file paths
        String base;

        try {
            base = PathFinder.getPath("data.out").getAbsolutePath() + "\\ReceivedData";
        } catch(IOException | IllegalArgumentException e) {
            errorHandler.handleCriticalError("Datawriter constructor", "Error while getting file paths", e);
            base = null;
        }

        baseFilename = base;

        getLastFileNumInFileSystem();
    }

    public void writeData(Observation obs) {
        String filename = getNextFilename();

        if(Files.exists(Path.of(filename))) {
            errorHandler.handleCriticalError("Datawriter.writeData()", "File " + filename + " already exists!",
                    new IllegalStateException("File " + filename + " already exists, possible error in getNextFilename()!"));
        }

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
        lastFilenum++;

        String result = baseFilename + lastFilenum + fileExtension;

        settings.setDataWriter_lastFileNum(lastFilenum);

        return result;
    }

    private void getLastFileNumInFileSystem() {
        try {
            String fileDirectory = PathFinder.getPath("data.out").getAbsolutePath();
            Stream<Path> stream = Files.list(Paths.get(fileDirectory));
            Set<String> fileSet = stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toSet());


            int lastFileNum = -1;

            for(String s : fileSet) {
                String temp = s.split("ReceivedData")[1];
                temp = temp.replace(".txt", "");
                int num = Integer.parseInt(temp);

                if(num > lastFileNum) {
                    lastFileNum = num;
                }
            }

            LOGGER.debug("Datawriter.getLastFileNumInFileSystem(): Set lastFileNum to " + lastFileNum);
            settings.setDataWriter_lastFileNum(lastFileNum);
        } catch(IOException | IllegalArgumentException e) {
            errorHandler.handleCriticalError("Datawriter.getLastFileNumInFileSystem()", "Error while getting last file num", e);
        }
    }
}
