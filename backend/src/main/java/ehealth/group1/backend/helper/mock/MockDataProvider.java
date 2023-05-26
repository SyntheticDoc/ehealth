package ehealth.group1.backend.helper.mock;

import ehealth.group1.backend.entity.ECGAnalysisResult;
import ehealth.group1.backend.entity.ECGHealthStatus;
import ehealth.group1.backend.entity.ECGStateHolderSettings;
import ehealth.group1.backend.entity.Settings;
import ehealth.group1.backend.enums.ECGSTATE;
import ehealth.group1.backend.repositories.SettingsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class MockDataProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final SettingsRepository settingsRepository;

    int iterationsToStateTransition;
    int iterationsToEmergencyCall;

    RingBuffer userman1Buf;
    RingBuffer userman2Buf;

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm:ss:SSS");

    private boolean hasData = false;

    public MockDataProvider(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    public ECGHealthStatus getForUser(String username) {
        if(!hasData) {
            init();
        }

        ECGSTATE current;

        if(username.equals("User Userman1")) {
            current = userman1Buf.getNext();
        } else if(username.equals("User Userman2")) {
            current = userman2Buf.getNext();
        } else {
            throw new IllegalArgumentException("No mock data for user " + username + " found!");
        }

        ECGAnalysisResult analysisResult = new ECGAnalysisResult();
        analysisResult.setEcgstate(current);
        analysisResult.setComment("MOCK DATA");
        analysisResult.setTimestamp(dtf.format(LocalDateTime.now()));

        ECGHealthStatus result = new ECGHealthStatus();
        result.setAssociatedUserName(username);
        result.setLastAnalysisResult(analysisResult);

        return result;
    }

    private ECGSTATE getRandomECGState(ECGSTATE[] allowedStates) {
        return allowedStates[ThreadLocalRandom.current().nextInt(0, allowedStates.length)];
    }

    private void init() {
        Settings settings = settingsRepository.findByUserId(0L);
        ECGStateHolderSettings stateHolderSettings = settings.getEcgStateHolderSettings();
        iterationsToStateTransition = stateHolderSettings.getIterationsToStateTransition();
        iterationsToEmergencyCall = stateHolderSettings.getIterationsToEmergencyCall();
        int beginningIterations = 0;
        int endIterations = 10;

        int bufSize = beginningIterations + iterationsToStateTransition + iterationsToEmergencyCall + endIterations;
        userman1Buf = new RingBuffer(bufSize);
        userman2Buf = new RingBuffer(bufSize);

        ECGSTATE[] allowedStatesUserman1 = new ECGSTATE[] {ECGSTATE.OK, ECGSTATE.OK, ECGSTATE.OK, ECGSTATE.OK, ECGSTATE.WARNING};

        for(int i = 0; i < bufSize; i++) {
            if(i < beginningIterations) {
                // Set beginning iteration values
                userman1Buf.memPut(getRandomECGState(allowedStatesUserman1), i);
                userman2Buf.memPut(ECGSTATE.OK, i);
            } else if(i < (beginningIterations + iterationsToStateTransition)) {
                // Set values here which are returned until the state transition
                userman1Buf.memPut(getRandomECGState(allowedStatesUserman1), i);
                userman2Buf.memPut(ECGSTATE.WARNING, i);
            } else if(i < (beginningIterations + iterationsToStateTransition + iterationsToEmergencyCall)) {
                // Set values here which are returned until the start of the emergency call
                userman1Buf.memPut(getRandomECGState(allowedStatesUserman1), i);
                userman2Buf.memPut(ECGSTATE.CRITICAL, i);
            } else {
                // Set values here while the emergency call is active
                userman1Buf.memPut(getRandomECGState(allowedStatesUserman1), i);
                userman2Buf.memPut(ECGSTATE.CALLEMERGENCY, i);
            }
        }
    }
}
