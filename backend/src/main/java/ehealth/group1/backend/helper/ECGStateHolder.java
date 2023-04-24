package ehealth.group1.backend.helper;

import ehealth.group1.backend.dto.ECGStateHolderSettings;
import ehealth.group1.backend.enums.ECGSTATE;
import org.hl7.fhir.r5.model.Observation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class ECGStateHolder {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    ECGSTATE current, last;
    Observation currentObservation;

    private ECGStateHolderSettings ecgStateHolderSettings;

    private int iterationsToStateTransitionLeft;
    private int iterationsToEmergencyCallLeft;

    public ECGStateHolder(ECGStateHolderSettings ecgStateHolderSettings) {
        this.ecgStateHolderSettings = ecgStateHolderSettings;
        resetTimers();
        current = ECGSTATE.OK;
        last = ECGSTATE.OK;
    }

    public void update(ECGSTATE analysisResult, Observation observation) {
        currentObservation = observation;
        last = current;
        current = analysisResult;

        //LOGGER.warn("STATE OLD - AnalysisResult: " + analysisResult + ", current: " + current + ", last: " + last);

        if(current == ECGSTATE.OK) {
            resetTimers();
            return;
        }

        if(current == ECGSTATE.WARNING && last == ECGSTATE.CRITICAL) {
            current = ECGSTATE.CRITICAL;
        }

        if(current == ECGSTATE.WARNING && last == ECGSTATE.CALLEMERGENCY) {
            current = ECGSTATE.CALLEMERGENCY;
            return;
        }

        if(current == ECGSTATE.WARNING && last == ECGSTATE.WARNING) {
            iterationsToStateTransitionLeft--;

            if(iterationsToStateTransitionLeft <= 0) {
                current = ECGSTATE.CRITICAL;
            }
        }

        if(current == ECGSTATE.CRITICAL) {
            iterationsToEmergencyCallLeft--;

            if(iterationsToEmergencyCallLeft <= 0) {
                current = ECGSTATE.CALLEMERGENCY;
            }
        }

        //LOGGER.warn("STATE NEW - AnalysisResult: " + analysisResult + ", current: " + current + ", last: " + last);
    }

    public void abortEmergency() {
        current = ECGSTATE.OK;
        last = ECGSTATE.OK;
        resetTimers();
    }

    public void updateSettings(ECGStateHolderSettings ecgStateHolderSettings) {
        LOGGER.warn("ECGStateHolderSettings resetted by updateSettings() call - this resets all timers in ECGStateHolder!");
        this.ecgStateHolderSettings = ecgStateHolderSettings;
        resetTimers();
    }

    public ECGSTATE getCurrent() {
        return current;
    }

    public Observation getCurrentObservation() {
        return currentObservation;
    }

    private void resetTimers() {
        iterationsToStateTransitionLeft = ecgStateHolderSettings.iterationsToStateTransition();
        iterationsToEmergencyCallLeft = ecgStateHolderSettings.iterationsToEmergencyCall();
    }
}
