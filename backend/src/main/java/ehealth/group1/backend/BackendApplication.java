package ehealth.group1.backend;

import ca.uhn.fhir.context.FhirContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;

@SpringBootApplication
public class BackendApplication {
  private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final FhirContext ctx;

  public BackendApplication(FhirContext ctx) {
    this.ctx = ctx;
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
              execTests();
          }
        }
      }
    };
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
