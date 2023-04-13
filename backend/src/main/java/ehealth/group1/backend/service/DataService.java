package ehealth.group1.backend.service;

import ca.uhn.fhir.context.FhirContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
public class DataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final FhirContext ctx;

    @Autowired
    public DataService(FhirContext ctx) {
        this.ctx = ctx;
    }
}
