package ehealth.group1.backend.service;

import ehealth.group1.backend.customfhirstructures.CustomObservation;
import ehealth.group1.backend.entity.ECGAnalysisResult;
import ehealth.group1.backend.entity.ECGAnalysisSettings;
import ehealth.group1.backend.entity.Settings;
import ehealth.group1.backend.enums.ECGSTATE;
import ehealth.group1.backend.helper.ErrorHandler;
import ehealth.group1.backend.helper.TransientServerSettings;
import ehealth.group1.backend.helper.datawriter.Datawriter;
import ehealth.group1.backend.helper.graphics.GraphicsModule;
import org.hl7.fhir.r5.model.Observation;
import org.hl7.fhir.r5.model.SampledData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

/**
 * Service to analyse ecg data.
 */
@Component
public class AnalyserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm:ss:SSS");

    private final ErrorHandler errorHandler;
    private final Datawriter datawriter;
    private final GraphicsModule graphicsModule;
    private final TransientServerSettings serverSettings;

    public AnalyserService(ErrorHandler errorHandler, Datawriter datawriter, GraphicsModule graphicsModule,
                           TransientServerSettings serverSettings) {
        this.errorHandler = errorHandler;
        this.datawriter = datawriter;
        this.graphicsModule = graphicsModule;
        this.serverSettings = serverSettings;
    }

    /**
     * Returns the state of the current ECG. If any of the components is ECGSTATE.OK, returns ECGSTATE.OK. Returns
     * nextState otherwise.
     *
     * @param obs The observation containing all ecg electrode components to be analysed
     * @return The ECGSTATE of the analysed observation
     */
    public ECGAnalysisResult analyse(CustomObservation obs, Settings settings) {
        Instant start = Instant.now();

        ECGSTATE[] stateList = new ECGSTATE[obs.getComponent().size()];
        ECGSTATE finalState = ECGSTATE.OK;

        String comment = "No comment";

        if(serverSettings.writeDataToDisk()) {
            datawriter.writeData(obs);
        }

        if(serverSettings.drawEcgData()) {
            graphicsModule.drawECG(obs.getComponent(), obs.getTimestampAsLocalDateTime());
        }

        for(int i = 0; i < obs.getComponent().size(); i++) {
            stateList[i] = analyseComponent(obs.getComponent().get(i), settings.getEcgAnalysisSettings());
        }

        int okStates = 0, invalidStates = 0, warningStates = 0;

        for(ECGSTATE s : stateList) {
            if(s == ECGSTATE.OK) {
                okStates++;
            } else if(s == ECGSTATE.INVALID) {
                invalidStates++;
            } else if(s == ECGSTATE.WARNING) {
                warningStates++;
            } else {
                invalidStates++;
                comment = "Invalid state detected: Analysis returned an unknown state.";
            }
        }

        Instant end = Instant.now();
        logTimeNeededForAnalysis(start, end);

        if(warningStates > 0) {
            if(invalidStates > 0) {
                finalState = ECGSTATE.INVALID;
            } else {
                finalState = ECGSTATE.WARNING;
            }
        } else if(invalidStates > 0) {
            finalState = ECGSTATE.INVALID;
        } else if(okStates < 0) {
            finalState = ECGSTATE.WARNING;
        } else {
            finalState = ECGSTATE.OK;
        }

        if(finalState == ECGSTATE.WARNING) {
            comment = "Possible asystole detected in at least one lead";
        }

        return new ECGAnalysisResult(finalState, dtf.format(LocalDateTime.now()), comment);
    }

    private void logTimeNeededForAnalysis(Instant start, Instant end) {
        long millisecondsNeeded = ChronoUnit.MILLIS.between(start, end);
        LOGGER.info("Analysis of ecg data needed " + millisecondsNeeded + " ms");
    }

    /**
     * Analyses the ecg data of a specific component and returns the result as ECGSTATE.
     *
     * @param c The component (ecg lead) holding the data to be analysed
     * @param ecgAnalysisSettings The settings object holding settings for the analyser
     * @return ECGSTATE.OK if no ecg abnormalities were detected, ECGSTATE.INVALID if the data could not be analysed,
     * possibly due to some data format error, ECGSTATE.WARNING if the analysed data looks like an asystole.
     */
    private ECGSTATE analyseComponent(Observation.ObservationComponentComponent c, ECGAnalysisSettings ecgAnalysisSettings) {
        SampledData rawData = c.getValueSampledData();
        double[] data;

        try {
            data = Arrays.stream(rawData.getData().trim().split(" ")).mapToDouble(Double::parseDouble).toArray();
        } catch(NumberFormatException e) {
            errorHandler.handleCustomException("AnalyserService.analyseComponent()", "SampledData contained invalid data", e);
            return ECGSTATE.INVALID;
        }

        LOGGER.info("Analyzing data:\n" + Arrays.toString(rawData.getData().split(" ")) + "\n");

        int largeDeviationCount = 0;

        for(int i = 0; i < (data.length - 1); i++) {
            if (Math.abs(data[i] - data[i + 1]) > ecgAnalysisSettings.getMaxDeviation()) {
                largeDeviationCount++;
            }

            if (largeDeviationCount > ecgAnalysisSettings.getMaxDeviationNum()) {
                LOGGER.info("Result OK");
                return ECGSTATE.OK;
            }
        }

        // not enough large deviations? Possible asystole!
        LOGGER.warn("Result WARNING");
        return ECGSTATE.WARNING;
    }
}
