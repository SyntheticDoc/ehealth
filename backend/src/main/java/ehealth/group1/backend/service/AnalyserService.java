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
import ehealth.group1.backend.helper.jely.JelyAnalyzer;
import ehealth.group1.backend.helper.jely.JelyAnalyzerResult;
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

    private final JelyAnalyzer jelyAnalyzer;
    private final ErrorHandler errorHandler;
    private final Datawriter datawriter;
    private final GraphicsModule graphicsModule;
    private final TransientServerSettings serverSettings;

    public AnalyserService(ErrorHandler errorHandler, Datawriter datawriter, GraphicsModule graphicsModule,
                           TransientServerSettings serverSettings, JelyAnalyzer jelyAnalyzer) {
        this.errorHandler = errorHandler;
        this.datawriter = datawriter;
        this.graphicsModule = graphicsModule;
        this.serverSettings = serverSettings;
        this.jelyAnalyzer = jelyAnalyzer;
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

        String comment = "No asystole detected";

        if(serverSettings.writeDataToDisk()) {
            datawriter.writeData(obs);
        }

        if(serverSettings.drawEcgData()) {
            graphicsModule.drawECG(obs.getComponent(), obs.getTimestampAsLocalDateTime());
        }

        StringBuilder jelyResults = new StringBuilder();

        for(int i = 0; i < obs.getComponent().size(); i++) {
            SampledData rawData = obs.getComponent().get(i).getValueSampledData();
            double[] data;
            ECGSTATE jelyState = ECGSTATE.INVALID;

            try {
                data = Arrays.stream(rawData.getData().trim().split(" ")).mapToDouble(Double::parseDouble).toArray();
            } catch(NumberFormatException e) {
                errorHandler.handleCustomException("AnalyserService.analyseComponent()", "SampledData contained invalid data", e);
                stateList[i] = ECGSTATE.INVALID;
                continue;
            }

            try {
                JelyAnalyzerResult jelyAnalyzerResult = jelyAnalyzer.analyze(data, obs.getComponent().get(i).getValueSampledData().getInterval().doubleValue());

                if(jelyAnalyzerResult.isWarning()) {
                    jelyState = ECGSTATE.WARNING;
                } else {
                    jelyState = ECGSTATE.OK;
                }

                jelyResults.append(jelyAnalyzerResult);
            } catch(Exception e) {
                jelyResults.append("Error in jelyAnalyzer: ").append(e.getMessage());
                //e.printStackTrace();
                throw new Error("Error in JelyAnalyzer");
            }

            if(i < (obs.getComponent().size() - 1)) {
                jelyResults.append("\n");
            }

            stateList[i] = analyseComponent(data, settings.getEcgAnalysisSettings());

            if(stateList[i] == ECGSTATE.OK && jelyState == ECGSTATE.WARNING) {
                stateList[i] = ECGSTATE.WARNING;
            }
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

        comment += "\nJELY results: " + jelyResults;

        LOGGER.info("Result comment:\n" + comment);

        return new ECGAnalysisResult(finalState, dtf.format(LocalDateTime.now()), comment);
    }

    private void logTimeNeededForAnalysis(Instant start, Instant end) {
        long millisecondsNeeded = ChronoUnit.MILLIS.between(start, end);
        LOGGER.info("Analysis of ecg data needed " + millisecondsNeeded + " ms");
    }

    /**
     * Analyses the ecg data of a specific component and returns the result as ECGSTATE.
     *
     * @param data The data of the component to be analysed
     * @param ecgAnalysisSettings The settings object holding settings for the analyser
     * @return ECGSTATE.OK if no ecg abnormalities were detected, ECGSTATE.INVALID if the data could not be analysed,
     * possibly due to some data format error, ECGSTATE.WARNING if the analysed data looks like an asystole.
     */
    private ECGSTATE analyseComponent(double[] data, ECGAnalysisSettings ecgAnalysisSettings) {
        LOGGER.info("Analyzing data:\n" + Arrays.toString(data) + "\n");

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
