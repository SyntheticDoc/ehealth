package ehealth.group1.backend;

import ca.uhn.fhir.context.FhirContext;
import ehealth.group1.backend.entity.Settings;
import ehealth.group1.backend.helper.dataloaders.TestDataLoader;
import ehealth.group1.backend.repositories.DataRepository;
import ehealth.group1.backend.repositories.SettingsRepository;
import ehealth.group1.backend.repositories.UserRepository;
import ehealth.group1.backend.service.ECGService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.lang.invoke.MethodHandles;
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

  private final ECGService ecgService;

  public BackendApplication(FhirContext ctx, SettingsRepository settingsRepository, DataRepository dataRepository,
                            UserRepository userRepository, TestDataLoader testDataLoader, ECGService ecgService) {
    this.ctx = ctx;
    this.settingsRepository = settingsRepository;
    this.dataRepository = dataRepository;
    this.userRepository = userRepository;
    this.testDataLoader = testDataLoader;
    this.ecgService = ecgService;
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

      if(args.length > 0) {
        LOGGER.info("Starting server with command line arguments: " + Arrays.toString(args));
        boolean writeDataToDisk = false;
        boolean drawEcgData = false;

        for (String arg : args) {
          // Code to execute if argument is present
          switch (arg) {
            case "test":
              testDataLoader.exec();
              //execHexIDTests();
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
          }
        }

        LOGGER.info("CommandLineRunner: writeDataToDisk=" + String.valueOf(writeDataToDisk).toUpperCase() + ",drawEcgData=" +
                String.valueOf(drawEcgData).toUpperCase());

        Settings settings = settingsRepository.findByUserId(0L);
        settings.setWriteDataToDisk(writeDataToDisk);
        settings.setDrawEcgData(drawEcgData);
        settingsRepository.save(settings);

        ecgService.reloadSettings();
      }
    };
  }

}
