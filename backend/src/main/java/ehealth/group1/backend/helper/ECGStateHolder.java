package ehealth.group1.backend.helper;

import ehealth.group1.backend.customfhirstructures.CustomObservation;
import ehealth.group1.backend.entity.ECGAnalysisResult;
import ehealth.group1.backend.entity.ECGStateHolderSettings;
import ehealth.group1.backend.entity.User;
import ehealth.group1.backend.enums.ECGSTATE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

public class ECGStateHolder {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    ECGSTATE current, last;
    ECGAnalysisResult currentAnalysisResult;
    CustomObservation currentObservation;

    private ECGStateHolderSettings ecgStateHolderSettings;
    private final String sourceDeviceIdentifier;
    private final User sourceUser;

    private int iterationsToStateTransitionLeft;
    private int iterationsToEmergencyCallLeft;

    public ECGStateHolder(ECGStateHolderSettings ecgStateHolderSettings, String sourceDeviceIdentifier, User sourceUser) {
        this.ecgStateHolderSettings = ecgStateHolderSettings;
        this.sourceDeviceIdentifier = sourceDeviceIdentifier;
        this.sourceUser = sourceUser;
        resetTimers();
        current = ECGSTATE.OK;
        last = ECGSTATE.OK;
    }

    public void update(ECGAnalysisResult currentAnalysisResult, CustomObservation observation) {
        currentObservation = observation;
        last = current;
        this.currentAnalysisResult = currentAnalysisResult;
        current = currentAnalysisResult.getEcgstate();

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

    public ECGAnalysisResult getCurrentAnalysisResult() {
        return currentAnalysisResult;
    }

    public CustomObservation getCurrentObservation() {
        return currentObservation;
    }

    public String getSourceDeviceIdentifier() {
        return sourceDeviceIdentifier;
    }

    public User getSourceUser() {
        return sourceUser;
    }

    private void resetTimers() {
        iterationsToStateTransitionLeft = ecgStateHolderSettings.getIterationsToStateTransition();
        iterationsToEmergencyCallLeft = ecgStateHolderSettings.getIterationsToEmergencyCall();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ECGStateHolder that = (ECGStateHolder) o;
        return current == that.current && last == that.last && Objects.equals(currentObservation, that.currentObservation) &&
                Objects.equals(ecgStateHolderSettings, that.ecgStateHolderSettings) &&
                sourceDeviceIdentifier.equals(that.sourceDeviceIdentifier) && sourceUser.equals(that.sourceUser);
    }

    @Override
    public int hashCode() {
        int result = 0;

        for(int i = 0; i < sourceDeviceIdentifier.length(); i++) {
            result = (result * 31) + sourceDeviceIdentifier.charAt(i);
        }

        return result;
    }
}
