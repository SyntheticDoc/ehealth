package ehealth.group1.backend;

import ca.uhn.fhir.context.FhirContext;
import ehealth.group1.backend.entity.Argon2Parameters;
import ehealth.group1.backend.entity.SecurityData;
import ehealth.group1.backend.entity.Settings;
import ehealth.group1.backend.generators.IDStringGenerator;
import ehealth.group1.backend.helper.ErrorHandler;
import ehealth.group1.backend.helper.PathFinder;
import ehealth.group1.backend.helper.TransientServerSettings;
import ehealth.group1.backend.helper.argon2crypto.Argon2ParameterChecker;
import ehealth.group1.backend.helper.argon2crypto.Argon2PasswordEncoderWithParams;
import ehealth.group1.backend.helper.dataloaders.DefaultDataLoader;
import ehealth.group1.backend.helper.dataloaders.TestDataLoader;
import ehealth.group1.backend.helper.graphics.GraphicsModule;
import ehealth.group1.backend.helper.wlan.WlanConnector;
import ehealth.group1.backend.repositories.*;
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

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;

@SpringBootApplication
@EntityScan("ehealth.group1.backend.*")
public class BackendApplication {
  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final FhirContext fhirctx;
  private final SettingsRepository settingsRepository;
  private final DataRepository dataRepository;
  private final UserRepository userRepository;
  private final SecurityDataRepository securityDataRepository;
  private final Argon2ParametersRepository argon2ParametersRepository;
  private final Argon2PasswordEncoderWithParams argon2PasswordEncoder;
  private final TestDataLoader testDataLoader;
  private final TransientServerSettings serverSettings;
  private ConfigurableEnvironment env;
  private WlanConnector wlanConnector;
  private final GraphicsModule graphicsModule;
  private final DefaultDataLoader defaultDataLoader;
  private final ErrorHandler errorHandler;

  private final ECGService ecgService;

  public BackendApplication(FhirContext fhirctx, SettingsRepository settingsRepository, DataRepository dataRepository,
                            UserRepository userRepository, SecurityDataRepository securityDataRepository,
                            Argon2ParametersRepository argon2ParametersRepository, Argon2PasswordEncoderWithParams argon2PasswordEncoder,
                            TestDataLoader testDataLoader, ECGService ecgService, TransientServerSettings serverSettings,
                            ConfigurableEnvironment env, WlanConnector wlanConnector, GraphicsModule graphicsModule,
                            DefaultDataLoader defaultDataLoader, ErrorHandler errorHandler) {
    this.fhirctx = fhirctx;
    this.settingsRepository = settingsRepository;
    this.dataRepository = dataRepository;
    this.userRepository = userRepository;
    this.securityDataRepository = securityDataRepository;
    this.argon2ParametersRepository = argon2ParametersRepository;
    this.argon2PasswordEncoder = argon2PasswordEncoder;
    this.testDataLoader = testDataLoader;
    this.ecgService = ecgService;
    this.serverSettings = serverSettings;
    this.env = env;
    this.wlanConnector = wlanConnector;
    this.graphicsModule = graphicsModule;
    this.defaultDataLoader = defaultDataLoader;
    this.errorHandler = errorHandler;
  }

  /**
   * The main entry point of the application. It initializes the Spring application context using the
   * `SpringApplicationBuilder` and runs the application with the specified arguments. The application context is
   * configured to be non-headless, allowing for graphical user interface components to be used.
   */
  public static void main(String[] args) {
    //SpringApplication.run(BackendApplication.class, args);
    SpringApplicationBuilder builder = new SpringApplicationBuilder(BackendApplication.class);
    builder.headless(false);
    ConfigurableApplicationContext ctx = builder.run(args);
  }

  /**
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
          LOGGER.warn("RUNNING SERVER WITH ARGUMENT " + arg);
          switch (arg) {
            case "test":
              if(false) {
                pathTester();
                return;
              }

              testArgon2();

              // Execute testDataLoader and set active profiles to testing
              testDataLoader.exec();
              env.setActiveProfiles(serverSettings.getPROFILE_TESTING(), serverSettings.getPROFILE_DEVELOPMENT());
              break;
            case "writeDataToDisk":
              // Enable writing data to disk
              writeDataToDisk = true;
              break;
            case "drawEcgData":
              // Enable drawing ECG data
              drawEcgData = true;
              break;
            case "writeAndDrawEcg":
              // Enable both writing data to disk and drawing ECG data
              writeDataToDisk = true;
              drawEcgData = true;
              break;
            case "wlan":
              // Get the address of the active Wlan-device of the current machine and switch server IP to this address
              wlanConnector.changeAddressToWlan();
              break;
          }
        }

        // Update the server settings based on the command line arguments
        serverSettings.setWriteDataToDisk(writeDataToDisk);
        serverSettings.setDrawEcgData(drawEcgData);

        // If drawEcgData is true, initialize graphics context
        if(drawEcgData) {
          graphicsModule.init();
        }
      }

      // Get the currently active profiles
      StringBuilder curProfiles = new StringBuilder();
      String[] activeProfiles = env.getActiveProfiles();

      if(activeProfiles.length > 0) {
        for(int i = 0; i < activeProfiles.length; i++) {
          curProfiles.append(String.format("%20s\n", activeProfiles[i]));
        }
      } else {
        curProfiles.append(String.format("%20s\n", "none"));
      }

      // Log the server settings and current active profiles
      LOGGER.info("CommandLineRunner executed.\n" + serverSettings + "\nCURRENT ACTIVE PROFILES:\n" + curProfiles);
      LOGGER.info("All done, HeartGuard is online and listening to the world!");
    };
  }

  private void testArgon2() {
    String logPrefix = "ARGON2 ENCODER TESTER: ";
    LOGGER.info(logPrefix + "Testing argon2 hashes...");
    SecurityData saltContainer = securityDataRepository.findByType("saltDefault");
    String salt = saltContainer.getVal();
    Argon2Parameters argon2Parameters = argon2ParametersRepository.findByType(Argon2ParameterChecker.getParamNameSlow());

    String testPwd = "testPwd";
    String testPwd2 = "testPwd";
    String testPwd3 = "testPwD";

    String testPwdHashed = argon2PasswordEncoder.encode(testPwd, argon2Parameters, salt);
    String testPwd2Hashed = argon2PasswordEncoder.encode(testPwd2, argon2Parameters, salt);
    String testPwd3Hashed = argon2PasswordEncoder.encode(testPwd3, argon2Parameters, salt);

    LOGGER.info(logPrefix + "Default salt:\n" + "\tsalt=" + salt);
    LOGGER.info(logPrefix + "Test passwords:\n" + "\ttestPwd=" + testPwd + "\n\ttestPwd2=" + testPwd2 + "\n\ttestPwd3=" + testPwd3);
    LOGGER.info(logPrefix + "Test passwords hashed:\n" + "\ttestPwdHashed=" + testPwdHashed + "\n\ttestPwd2Hashed=" + testPwd2Hashed +
            "\n\ttestPwd3Hashed=" + testPwd3Hashed);

    if(argon2PasswordEncoder.matches(testPwd2, testPwdHashed)) {
      LOGGER.info(logPrefix + "testPwd2 matches testPwdHashed, all is well!");
    } else {
      errorHandler.handleCriticalError("BackendApplication.testArgon2()",
              "testPwd2 does not match testPwdHashed!",
              new Exception("testPwd2 does not match testPwdHashed!"));
    }

    if(argon2PasswordEncoder.matches(testPwd3, testPwdHashed)) {
      errorHandler.handleCriticalError("BackendApplication.testArgon2()",
              "testPwd3 matches testPwdHashed!",
              new Exception("testPwd2 matches testPwdHashed!"));
    } else {
      LOGGER.info(logPrefix + "testPwd3 does not match testPwdHashed, all is well!");
    }

    LOGGER.info(logPrefix + "Argon 2 test successful!");
  }

  private void pathTester() {
    try {
      File f = PathFinder.getPath("CustomObservationTemplate");
      LOGGER.warn("pathTester path: " + f.getAbsolutePath());
      LOGGER.warn("File exists? " + f.exists());

      System.out.println("\n\n");

      Scanner sc = new Scanner(f);

      while(sc.hasNextLine()) {
        System.out.println(sc.nextLine());
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
}
