package ehealth.group1.backend;

import ca.uhn.fhir.context.FhirContext;
import ehealth.group1.backend.jely.JELYmaster.de.fau.mad.jely.*;
import ehealth.group1.backend.jely.JELYmaster.de.fau.mad.jely.detectors.HeartbeatDetector;
import ehealth.group1.backend.jely.JELYmaster.de.fau.mad.jely.io.FileLoader;
import ehealth.group1.backend.generators.IDStringGenerator;
import ehealth.group1.backend.helper.dataloaders.TestDataLoader;
import ehealth.group1.backend.repositories.DataRepository;
import ehealth.group1.backend.repositories.SettingsRepository;
import ehealth.group1.backend.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;

@SpringBootApplication
@EntityScan("ehealth.group1.backend.*")
public class BackendApplication {
  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final FhirContext ctx;
  private final SettingsRepository settingsRepository;
  private final DataRepository dataRepository;
  private final UserRepository userRepository;
  private final TestDataLoader testDataLoader;

  public BackendApplication(FhirContext ctx, SettingsRepository settingsRepository, DataRepository dataRepository, UserRepository userRepository, TestDataLoader testDataLoader) {
    this.ctx = ctx;
    this.settingsRepository = settingsRepository;
    this.dataRepository = dataRepository;
    this.userRepository = userRepository;
    this.testDataLoader = testDataLoader;
  }

  public static void main(String[] args) {
    SpringApplication.run(BackendApplication.class, args);
  }

  /*
    Bean for getting and interpreting command line arguments handed via -Dspring-boot.run.arguments="##ARGUMENT##"
  */
  @Bean
  public CommandLineRunner commandLineRunnerBean() {
    return (args) -> {

      if(args.length > 0) {
        LOGGER.info("Starting server with command line arguments: " + Arrays.toString(args));

        for (String arg : args) {
          // Code to execute if argument is present
          switch (arg) {
            case "test":
              testDataLoader.exec();
              jelyTest();
          }
        }
      }
    };
  }

  private void jelyTest() {
    Ecglib.setDebugMode(true);
    LOGGER.info("EcgLib.isDebugMode? " + Ecglib.isDebugMode());
    Ecg ecgFile = FileLoader.loadKnownEcgFile("src/main/java/ehealth/group1/backend/jely/testfiles/jelyecg4.csv",
            LeadConfiguration.SINGLE_UNKNOWN_LEAD, 125);
    LOGGER.info("\n\necgFile: " + ecgFile.toString() + "\n\n");
    HeartbeatDetector detector = new HeartbeatDetector(ecgFile, (HeartbeatDetector.HeartbeatDetectionListener) null);
    ArrayList<Heartbeat> beatList = detector.findHeartbeats();

    LOGGER.info("\n\nBeatlist: " + Arrays.toString(beatList.toArray()) + "\n\n");

    for(Heartbeat h : beatList) {
      QrsComplex qrs = h.getQrs();
      int rPeak = qrs.getRPosition();
      LOGGER.info("\n\nHeartbeat: " + h.toString() + "\nQRS: " + qrs.toString() + "\nR-peak: " + rPeak + "\n\n");
    }
  }

}
