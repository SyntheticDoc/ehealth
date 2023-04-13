package ehealth.group1.backend.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ehealth.group1.backend.helper.ErrorHandler;
import org.hl7.fhir.r5.model.Observation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
public class DataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final FhirContext ctx;
    private final ErrorHandler errorHandler;

    // TODO: Check if autowired is really necessary here
    @Autowired
    public DataService(FhirContext ctx, ErrorHandler errorHandler) {
        this.ctx = ctx;
        this.errorHandler = errorHandler;
        getObservation("Bla");
    }

    public Observation getObservation(Observation obs) {
        // TODO: Validate Observation received
        return obs;
    }

    public Observation getObservation(String JsonData) {
        // TODO: Validate JSON contents and format
        IParser parser = ctx.newJsonParser();
        Observation obs;

        try {
            obs = parser.parseResource(Observation.class, JsonData);
        } catch(Exception e) {
            errorHandler.handleCriticalError("DataService.getObservation(String JsonData)",
                    "Could not parse JSON data!", e);
            return null;
        }

        return obs;
    }
}
