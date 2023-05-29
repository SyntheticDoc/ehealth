package ehealth.group1.backend.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ehealth.group1.backend.customfhirstructures.CustomObservation;
import ehealth.group1.backend.helper.ErrorHandler;
import ehealth.group1.backend.helper.PathFinder;
import ehealth.group1.backend.repositories.DeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

@Component
public class DataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final FhirContext ctx;
    private final ErrorHandler errorHandler;
    private final DeviceRepository deviceRepository;

    public DataService(FhirContext ctx, ErrorHandler errorHandler, DeviceRepository deviceRepository) {
        this.ctx = ctx;
        this.errorHandler = errorHandler;
        this.deviceRepository = deviceRepository;
    }

    public CustomObservation getObservation(CustomObservation obs) {
        // TODO: Validate Observation received
        return obs;
    }

    public CustomObservation getObservation(String JsonData) {
        IParser parser = ctx.newJsonParser();
        return parser.parseResource(CustomObservation.class, JsonData);
    }

    public CustomObservation getObservation_fromCustomEsp32(String JsonData) {
        // Build CustomObservation template to be filled with JsonData
        CustomObservation obs;
        ArrayList<String> template = getCustomObservationTemplate();

        // Parse JsonData
        JsonParser springParser = JsonParserFactory.getJsonParser();
        Map<String, Object> parsedData = springParser.parseMap(JsonData);

        // TODO: Remove debug-for-each
        String p = "PARSED DATA:\n\n";

        for(Map.Entry<String, Object> e : parsedData.entrySet()) {
            p += e.getKey() + " : " + e.getValue() + "\n";
        }

        LOGGER.debug(p);

        // Fill template with JsonData
        StringBuilder obsData = new StringBuilder();
        IParser parser = ctx.newJsonParser();
        String componentIdentifier = deviceRepository.findECGDeviceByIdentifier(parsedData.get("identifier").toString())
                .getComponents().get(0).getIdentifier();

        for(String s : template) {
            if(s.contains("\"valueDateTime\": \"nodata\"")) {
                obsData.append(s.replace("nodata", parsedData.get("timestamp").toString()));
            } else if(s.contains("\"valueString\": \"nodata\"")) {
                obsData.append(s.replace("nodata", parsedData.get("identifier").toString()));
            } else if(s.contains("\"code\": \"nodata\",")) {
                obsData.append(s.replace("nodata", componentIdentifier));
            } else if(s.contains("\"data\": \"nodata\"")) {
                obsData.append(s.replace("nodata", parsedData.get("data").toString()));
            } else if(s.contains("\"interval\": 10")) {
                // Calculate sampling interval first from sampling rate in Hz
                String interval = String.valueOf(1000 / Double.parseDouble(parsedData.get("sampling").toString()));
                obsData.append(s.replace("10", interval));
            } else {
                obsData.append(s);
            }
        }

        obs = parser.parseResource(CustomObservation.class, obsData.toString());

        return obs;
    }

    private void validateTimestamp(LocalDateTime timestamp) {
        // TODO: Validate
    }

    private ArrayList<String> getCustomObservationTemplate() {
        String filename = null;
        ArrayList<String> result = new ArrayList<>();

        try {
            filename = PathFinder.getPath("CustomObservationTemplate").getAbsolutePath();
        } catch(IOException | IllegalArgumentException e) {
            errorHandler.handleCriticalError("DataService.getCustomObservationTemplate()", "Error while getting file path", e);
        }

        try {
            Scanner sc = new Scanner(new File(filename));

            while(sc.hasNextLine()) {
                result.add(sc.nextLine() + "\n");
            }

            return result;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
