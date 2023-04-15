package ehealth.group1.backend.helper;

import ehealth.group1.backend.enums.ECGSTATE;
import org.hl7.fhir.r5.model.Observation;

public class ECGStateHolder {
    ECGSTATE current, last;
    Observation currentObservation;

    // TODO: Get from user settings
    private int iterationsToStateTransitionLeft = 5;
    private int iterationsToEmergencyCallLeft = 30;

    public ECGStateHolder() {
        current = ECGSTATE.OK;
        last = ECGSTATE.OK;
    }

    public void update(ECGSTATE analysisResult, Observation observation) {
        currentObservation = observation;
        last = current;
        current = analysisResult;

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
    }

    public void abortEmergency() {
        current = ECGSTATE.OK;
        last = ECGSTATE.OK;
    }

    public ECGSTATE getCurrent() {
        return current;
    }

    public Observation getCurrentObservation() {
        return currentObservation;
    }
}
