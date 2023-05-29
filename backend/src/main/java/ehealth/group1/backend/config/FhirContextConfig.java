package ehealth.group1.backend.config;

import ca.uhn.fhir.context.FhirContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.invoke.MethodHandles;

/**
 * The `FhirContextConfig` class is a configuration class responsible for defining a bean that provides an
 * instance of the `FhirContext`. This context is used for working with FHIR (Fast Healthcare Interoperability
 * Resources) in the application. The `fhirContext()` method initializes and returns an instance of the
 * `FhirContext` configured for FHIR R5 version, using a cached implementation for improved performance.
 *
 * This config is needed, because creating a FhirContext is very time-consuming. Therefore, we only generate this
 * context once when the application starts and reuse it throughout the running time of the server.
 */
@Configuration
public class FhirContextConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private FhirContext ctx = null;

    @Bean
    public FhirContext fhirContext() {
        LOGGER.info("Bean instantiation \"fhirContext \" in \"FhirContextConfig\" was called");
        return FhirContext.forR5Cached();
    }
}
