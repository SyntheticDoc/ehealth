package ehealth.group1.backend;

import ca.uhn.fhir.context.FhirContext;
import ehealth.group1.backend.dto.ECGAnalysisSettings;
import ehealth.group1.backend.dto.ECGStateHolderSettings;
import ehealth.group1.backend.dto.Settings;
import ehealth.group1.backend.entity.User;
import ehealth.group1.backend.generators.IDStringGenerator;
import ehealth.group1.backend.persistence.DataDao;
import ehealth.group1.backend.persistence.SettingsDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class BackendApplication {
  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final FhirContext ctx;
  private final SettingsDao settingsDao;
  private final DataDao dataDao;

  public BackendApplication(FhirContext ctx, SettingsDao settingsDao, DataDao dataDao) {
    this.ctx = ctx;
    this.settingsDao = settingsDao;
    this.dataDao = dataDao;
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
              execHexIDTests();
          }
        }
      }
    };
  }

  private void execHexIDTests() {
    for(int i = 0; i < 5; i++) {
      LOGGER.warn("Generated hex id: " + IDStringGenerator.getNewIDString());
    }
  }

  private void execDBTests2() {
    User user = new User(57L, "NewUser", "ExampleAddress", 42L, true, "pwd");
    LOGGER.info("Creating new user: " + user);

    dataDao.createUser(user);

    LOGGER.info("User created: " + dataDao.searchUser(user).get(0));

    Long userId = dataDao.searchUser(user).get(0).getId();

    User user2 = new User(userId, "NewUser2", "ExampleAddress2", 43L, false, "pwd2");

    dataDao.updateUser(user2);

    LOGGER.info("User updated: " + dataDao.searchUser(user2).get(0));

    User user3 = new User(null, "NewUser2", "ExampleAddress2", 43L, false, "pwd2");

    List<User> searchResult = dataDao.searchUser(user3);

    LOGGER.info("User search result: " + searchResult.get(0));
    LOGGER.info("Search result list length: " + searchResult.size());

    dataDao.deleteUser(user2);

    LOGGER.info("User exists?: " + dataDao.userExists(user2.getId()));
  }

  private void execDBTests() {
    LOGGER.warn("Executing database tests...");

    Settings settings = settingsDao.getForUserId(0L).get(0);
    ECGStateHolderSettings stateholder = settings.ecgStateHolderSettings();
    ECGAnalysisSettings analysis = settings.ecgAnalysisSettings();

    User user = dataDao.getOneById(0L).get(0);

    StringBuilder s = new StringBuilder();

    s.append("\n\n");
    s.append("  Settings - id: ").append(settings.id()).append("\n");
    s.append("  Settings - user_id: ").append(settings.user_id()).append("\n");
    s.append("  Stateholder - id: ").append(stateholder.id()).append("\n");
    s.append("  Stateholder - user_id: ").append(stateholder.user_id()).append("\n");
    s.append("  Stateholder - iterations_transition: ").append(stateholder.iterationsToStateTransition()).append("\n");
    s.append("  Stateholder - iterations_emergency: ").append(stateholder.iterationsToEmergencyCall()).append("\n");
    s.append("  Analysis - id: ").append(analysis.id()).append("\n");
    s.append("  Analysis - user_id: ").append(analysis.user_id()).append("\n");
    s.append("  Analysis - maxDeviations: ").append(analysis.maxDeviation()).append("\n");
    s.append("  Analysis - maxDeviations_num: ").append(analysis.maxDeviationNum()).append("\n");
    s.append("--------------------------------\n");
    s.append("  User - id: ").append(user.getId()).append("\n");
    s.append("  User - name: ").append(user.getName()).append("\n");
    s.append("  User - address: ").append(user.getAddress()).append("\n");
    s.append("  User - phone: ").append(user.getPhone()).append("\n");
    s.append("  User - emergency: ").append(user.getEmergency()).append("\n");
    s.append("  User - password: ").append(user.getPassword()).append("\n");

    LOGGER.info("Data:" + s);
  }

  private void execTests() {
    LOGGER.warn("Executing quick startup tests...");

    LOGGER.info("Creating fhir contexts...");

    FhirContext c1 = ctx;
    FhirContext c2 = FhirContext.forR5Cached();
    FhirContext c3 = FhirContext.forR5Cached();
    FhirContext c4 = FhirContext.forR5();
    FhirContext c5 = FhirContext.forR5Cached();

    LOGGER.info("Context c1: " + c1.hashCode());
    LOGGER.info("Context c2: " + c2.hashCode());
    LOGGER.info("Context c3: " + c3.hashCode());
    LOGGER.info("Context c4: " + c4.hashCode());
    LOGGER.info("Context c5: " + c5.hashCode());

    LOGGER.info("Startup tests finished.");
  }
}
