package ehealth.group1.backend.service;

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
    public ECGSTATE analyse(Observation obs) {
        Instant start = Instant.now();

        ECGSTATE state;

        ECGSTATE[] stateList = new ECGSTATE[obs.getComponent().size()];

        for(int i = 0; i < obs.getComponent().size(); i++) {
            stateList[i] = analyseComponent(obs.getComponent().get(i));
        }

        for(ECGSTATE s : stateList) {
            if(s == ECGSTATE.OK) {
                state = ECGSTATE.OK;
                break;
            } else if(s == ECGSTATE.INVALID) {
                state = ECGSTATE.INVALID;
                break;
            }
        }

        Instant end = Instant.now();

        return ECGSTATE.WARNING;
    }

    private ECGSTATE analyseComponent(Observation.ObservationComponentComponent c) {
        SampledData rawData = c.getValueSampledData();
        int[] data = Arrays.stream(rawData.getData().split(" ")).mapToInt(Integer::parseInt).toArray();
//        BigDecimal interval = rawData.getInterval();
//        String intervalUnit = rawData.getIntervalUnit();

        // TODO: Get this from settings
        int maxDeviation = 10;
        int maxDeviationNum = 5;

//        if(!intervalUnit.equals("ms")) {
//            InvalidIntervalUnitException e = new InvalidIntervalUnitException("In component " + c.getCode().getCoding().get(0).getDisplay() +
//                    ": Unknown interval unit \"" + intervalUnit + "\". Can't process this component.");
//            errorHandler.handleCustomException("AnalyserService.analyseComponent()", "Unknown interval unit", e);
//            return ECGSTATE.INVALID;
//        }

        int largeDeviationCount = 0;

        for(int i = 0; i < (data.length - 1); i++) {
            if(Math.abs(data[i] - data[i+1]) > maxDeviation) {
                largeDeviationCount++;
            }

            if(largeDeviationCount > maxDeviationNum) {
                return ECGSTATE.OK;
            }
        }

        return ECGSTATE.WARNING;
    }
}
