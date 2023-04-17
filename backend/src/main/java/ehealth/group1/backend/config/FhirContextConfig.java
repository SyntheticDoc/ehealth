package ehealth.group1.backend.config;

import ca.uhn.fhir.context.FhirContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.invoke.MethodHandles;

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
