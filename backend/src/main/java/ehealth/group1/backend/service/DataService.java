package ehealth.group1.backend.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ehealth.group1.backend.customfhirstructures.CustomObservation;
import ehealth.group1.backend.exception.HeaderParseException;
import ehealth.group1.backend.helper.ErrorHandler;
import org.hl7.fhir.r5.model.Observation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class DataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final FhirContext ctx;
    private final ErrorHandler errorHandler;

    public DataService(FhirContext ctx, ErrorHandler errorHandler) {
        this.ctx = ctx;
        this.errorHandler = errorHandler;
    }

    public CustomObservation getObservation(CustomObservation obs) {
        // TODO: Validate Observation received
        return obs;
    }

    public CustomObservation getObservation(String JsonData) {
        IParser parser = ctx.newJsonParser();
        return parser.parseResource(CustomObservation.class, JsonData);
    }

    /*public Observation getObservation(String JsonData) {
        // TODO: Validate JSON contents and format
        IParser parser = ctx.newJsonParser();
        Observation obs;

        String[] dataString = JsonData.split("\n", 2);
        String header = dataString[0];
        String data = dataString[1];

        String[] parsedHeader = parseHeader(header);
        int checksum;
        LocalDateTime timestamp;

        try {
            checksum = Integer.parseInt(parsedHeader[0]);
        } catch(NumberFormatException e) {
            errorHandler.handleCustomException("DataService.getObservation()", "Could not parse header, checksum was no " +
                    "integer. (Checksum received: " + parsedHeader[0] + ")", e);
            throw e;
        }

        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm:ss:SSS");
            timestamp = LocalDateTime.parse(parsedHeader[1], dtf);
        } catch(DateTimeParseException e) {
            errorHandler.handleCustomException("DataService.getObservation()", "Could not parse header, timestamp not " +
                    " readable. (Timestamp received: " + parsedHeader[1] + ")", e);
            throw e;
        }

        LOGGER.info("Checksum: " + checksum);
        LOGGER.info("Timestamp: " + timestamp);

        validateChecksum(checksum, timestamp, data);
        // Get timestamp from last received data to verify that this data is indeed newer
        validateTimestamp(timestamp);

        try {
            obs = parser.parseResource(Observation.class, data);
        } catch(Exception e) {
            errorHandler.handleCriticalError("DataService.getObservation(String JsonData)",
                    "Could not parse JSON data!\nData provided: " + JsonData + "\n", e);
            return null;
        }

        return obs;
    }*/

    private void validateChecksum(int checksum, LocalDateTime timestamp, String data) {
        // TODO: Validate
    }

    private void validateTimestamp(LocalDateTime timestamp) {
        // TODO: Validate
    }

    private String[] parseHeader(String header) {
        String[] result = new String[2];
        String[] temp = header.split(",");

        if(temp.length != 2) {
            HeaderParseException e = new HeaderParseException("Could not parse header, length after splitting was " + temp.length + " (expected: 2)");
            errorHandler.handleCustomException("DataService.parseHeader()", "Error parsing JSON header (couldn't read header)", e);
            throw e;
        }

        result[0] = temp[0].split(":")[1].replace("\"", "").replace("}", "");
        result[1] = temp[1].split(":", 2)[1].replace("\"", "").replace("}", "");

        return result;
    }
}
