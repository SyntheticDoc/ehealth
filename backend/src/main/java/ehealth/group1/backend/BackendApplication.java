package ehealth.group1.backend;

import ca.uhn.fhir.context.FhirContext;
import ehealth.group1.backend.entity.Settings;
import ehealth.group1.backend.generators.IDStringGenerator;
import ehealth.group1.backend.helper.TransientServerSettings;
import ehealth.group1.backend.helper.dataloaders.TestDataLoader;
import ehealth.group1.backend.helper.wlan.WlanConnector;
import ehealth.group1.backend.repositories.DataRepository;
import ehealth.group1.backend.repositories.SettingsRepository;
import ehealth.group1.backend.repositories.UserRepository;
import ehealth.group1.backend.service.ECGService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;

@SpringBootApplication
@EntityScan("ehealth.group1.backend.*")
public class BackendApplication {
  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final FhirContext fhirctx;
  private final SettingsRepository settingsRepository;
  private final DataRepository dataRepository;
  private final UserRepository userRepository;
  private final TestDataLoader testDataLoader;
  private final TransientServerSettings serverSettings;
  private ConfigurableEnvironment env;
  private WlanConnector wlanConnector;

  private final ECGService ecgService;

  public BackendApplication(FhirContext fhirctx, SettingsRepository settingsRepository, DataRepository dataRepository,
                            UserRepository userRepository, TestDataLoader testDataLoader, ECGService ecgService,
                            TransientServerSettings serverSettings, ConfigurableEnvironment env, WlanConnector wlanConnector) {
    this.fhirctx = fhirctx;
    this.settingsRepository = settingsRepository;
    this.dataRepository = dataRepository;
    this.userRepository = userRepository;
    this.testDataLoader = testDataLoader;
    this.ecgService = ecgService;
    this.serverSettings = serverSettings;
    this.env = env;
    this.wlanConnector = wlanConnector;
  }

  public static void main(String[] args) {
    //SpringApplication.run(BackendApplication.class, args);
    SpringApplicationBuilder builder = new SpringApplicationBuilder(BackendApplication.class);
    builder.headless(false);
    ConfigurableApplicationContext ctx = builder.run(args);
  }

  /*
    Bean for getting and interpreting command line arguments handed via -Dspring-boot.run.arguments="##ARGUMENT##"
  */
  @Bean
  public CommandLineRunner commandLineRunnerBean() {
    return (args) -> {
      // TODO: Change to PROFILE_PRODUCTION for a production-ready server
      // env.setActiveProfiles(serverSettings.getPROFILE_PRODUCTION());
      env.setActiveProfiles(serverSettings.getPROFILE_DEVELOPMENT());

      if(args.length > 0) {
        LOGGER.info("Starting server with command line arguments: " + Arrays.toString(args));
        boolean writeDataToDisk = false;
        boolean drawEcgData = false;

        for (String arg : args) {
          // Code to execute if argument is present
          switch (arg) {
            case "test":
              testDataLoader.exec();
              env.setActiveProfiles(serverSettings.getPROFILE_TESTING());
              break;
            case "writeDataToDisk":
              writeDataToDisk = true;
              break;
            case "drawEcgData":
              drawEcgData = true;
              break;
            case "writeAndDrawEcg":
              writeDataToDisk = true;
              drawEcgData = true;
              break;
            case "wlan":
              wlanConnector.changeAddressToWlan();
              break;
          }
        }

        serverSettings.setWriteDataToDisk(writeDataToDisk);
        serverSettings.setDrawEcgData(drawEcgData);
      }

      StringBuilder curProfiles = new StringBuilder();
      String[] activeProfiles = env.getActiveProfiles();

      if(activeProfiles.length > 0) {
        for(int i = 0; i < activeProfiles.length; i++) {
          curProfiles.append(String.format("%20s\n", activeProfiles[i]));
        }
      } else {
        curProfiles.append(String.format("%20s\n", "none"));
      }

      LOGGER.info("CommandLineRunner executed.\n" + serverSettings + "\nCURRENT ACTIVE PROFILES:\n" + curProfiles);
      LOGGER.info("All done, HeartGuard is online and listening to the world!");
    };
  }
}
