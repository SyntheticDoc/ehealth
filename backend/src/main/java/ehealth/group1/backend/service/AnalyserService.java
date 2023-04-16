package ehealth.group1.backend.service;

import ehealth.group1.backend.dto.ECGAnalysisSettings;
import ehealth.group1.backend.enums.ECGSTATE;
import ehealth.group1.backend.exception.InvalidIntervalUnitException;
import ehealth.group1.backend.helper.ErrorHandler;
import org.hl7.fhir.r5.model.Observation;
import org.hl7.fhir.r5.model.SampledData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

@Component
public class AnalyserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ErrorHandler errorHandler;

    public AnalyserService(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    /**
     * Returns the state of the current ECG. If any of the components is ECGSTATE.OK, returns ECGSTATE.OK. Returns
     * nextState otherwise.
     *
     * @param obs The observation containing all ecg electrode components to be analysed
     * @return The ECGSTATE of the analysed observation
     */
    public ECGSTATE analyse(Observation obs, ECGAnalysisSettings ecgAnalysisSettings) {
        Instant start = Instant.now();

        ECGSTATE[] stateList = new ECGSTATE[obs.getComponent().size()];

        for(int i = 0; i < obs.getComponent().size(); i++) {
            stateList[i] = analyseComponent(obs.getComponent().get(i), ecgAnalysisSettings);
        }

        for(ECGSTATE s : stateList) {
            if(s == ECGSTATE.OK) {
                return ECGSTATE.OK;
            } else if(s == ECGSTATE.INVALID) {
                return ECGSTATE.INVALID;
            }
        }

        Instant end = Instant.now();

        long millisecondsNeeded = ChronoUnit.MILLIS.between(end, start);
        LOGGER.debug("Analysis of ecg data needed " + millisecondsNeeded + " ms");

        return ECGSTATE.WARNING;
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

        int largeDeviationCount = 0;

        for(int i = 0; i < (data.length - 1); i++) {
            if(Math.abs(data[i] - data[i+1]) > ecgAnalysisSettings.maxDeviation()) {
                largeDeviationCount++;
            }

            if(largeDeviationCount > ecgAnalysisSettings.maxDeviationNum()) {
                return ECGSTATE.OK;
            }
        }

        // not enough large deviations? Possible asystole!
        return ECGSTATE.WARNING;
    }
}
