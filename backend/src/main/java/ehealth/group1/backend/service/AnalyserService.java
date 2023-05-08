package ehealth.group1.backend.service;

import ehealth.group1.backend.customfhirstructures.CustomObservation;
import ehealth.group1.backend.entity.ECGAnalysisSettings;
import ehealth.group1.backend.entity.Settings;
import ehealth.group1.backend.enums.ECGSTATE;
import ehealth.group1.backend.helper.ErrorHandler;
import ehealth.group1.backend.helper.datawriter.Datawriter;
import ehealth.group1.backend.helper.graphics.GraphicsModule;
import org.hl7.fhir.r5.model.Observation;
import org.hl7.fhir.r5.model.SampledData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

@Component
public class AnalyserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ErrorHandler errorHandler;
    private final Datawriter datawriter;
    private final GraphicsModule graphicsModule;

    public AnalyserService(ErrorHandler errorHandler, Datawriter datawriter, GraphicsModule graphicsModule) {
        this.errorHandler = errorHandler;
        this.datawriter = datawriter;
        this.graphicsModule = graphicsModule;
    }

    /**
     * Returns the state of the current ECG. If any of the components is ECGSTATE.OK, returns ECGSTATE.OK. Returns
     * nextState otherwise.
     *
     * @param obs The observation containing all ecg electrode components to be analysed
     * @return The ECGSTATE of the analysed observation
     */
    public ECGSTATE analyse(CustomObservation obs, Settings settings) {
        Instant start = Instant.now();

        ECGSTATE[] stateList = new ECGSTATE[obs.getComponent().size()];

        if(settings.writeDataToDisk()) {
            datawriter.writeData(obs);
        }

        if(settings.drawEcgData()) {
            graphicsModule.drawECG(obs.getComponent(), obs.getTimestampAsLocalDateTime());
        }

        for(int i = 0; i < obs.getComponent().size(); i++) {
            stateList[i] = analyseComponent(obs.getComponent().get(i), settings.getEcgAnalysisSettings());
        }

        for(ECGSTATE s : stateList) {
            if(s == ECGSTATE.OK) {
                Instant end = Instant.now();
                logTimeNeededForAnalysis(start, end);
                return ECGSTATE.OK;
            } else if(s == ECGSTATE.INVALID) {
                Instant end = Instant.now();
                logTimeNeededForAnalysis(start, end);
                return ECGSTATE.INVALID;
            }
        }

        Instant end = Instant.now();
        logTimeNeededForAnalysis(start, end);

        return ECGSTATE.WARNING;
    }

    private void logTimeNeededForAnalysis(Instant start, Instant end) {
        long millisecondsNeeded = ChronoUnit.MILLIS.between(start, end);
        LOGGER.info("Analysis of ecg data needed " + millisecondsNeeded + " ms");
    }

    private ECGSTATE analyseComponent(Observation.ObservationComponentComponent c, ECGAnalysisSettings ecgAnalysisSettings) {
        SampledData rawData = c.getValueSampledData();
        int[] data = Arrays.stream(rawData.getData().split(" ")).mapToInt(Integer::parseInt).toArray();
//        BigDecimal interval = rawData.getInterval();
//        String intervalUnit = rawData.getIntervalUnit();

//        if(!intervalUnit.equals("ms")) {
//            InvalidIntervalUnitException e = new InvalidIntervalUnitException("In component " + c.getCode().getCoding().get(0).getDisplay() +
//                    ": Unknown interval unit \"" + intervalUnit + "\". Can't process this component.");
//            errorHandler.handleCustomException("AnalyserService.analyseComponent()", "Unknown interval unit", e);
//            return ECGSTATE.INVALID;
//        }

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
