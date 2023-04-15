package ehealth.group1.backend.helper;

import ehealth.group1.backend.enums.ECGSTATE;

public class ECGStateHolder {
    ECGSTATE current, last;

    // TODO: Get from user settings
    private int iterationsToStateTransitionLeft = 5;
    private int iterationsToEmergencyCallLeft = 30;

    public ECGStateHolder() {
        current = ECGSTATE.OK;
        last = ECGSTATE.OK;
    }

    public void update(ECGSTATE analysisResult) {
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
}
