package ehealth.group1.backend.helper.jely;

import ehealth.group1.backend.jely.JELYmaster.de.fau.mad.jely.*;
import ehealth.group1.backend.jely.JELYmaster.de.fau.mad.jely.detectors.HeartbeatDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;

@Component
public class JelyAnalyzer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public void analyze(double[] ecgData) {
        LOGGER.info("Starting JelyAnalyzer...");
        Ecglib.setDebugMode(true);
        LOGGER.info("EcgLib.isDebugMode? " + Ecglib.isDebugMode());
        //Ecg ecgFile = FileLoader.loadKnownEcgFile("src/main/java/ehealth/group1/backend/jely/testfiles/jelyecg4.csv", LeadConfiguration.SINGLE_UNKNOWN_LEAD, 125);

        Ecg ecgFile = new Ecg(ecgData, 125.0, EcgLead.UNKNOWN);

        //LOGGER.info("\n\necgFile: " + Arrays.toString(ecgFile.getSignal(0).getSignal().toArray()) + "\n\n");
        HeartbeatDetector detector = new HeartbeatDetector(ecgFile, (HeartbeatDetector.HeartbeatDetectionListener) null);
        ArrayList<Heartbeat> beatList = detector.findHeartbeats();

        LOGGER.info("\n\nBeatlist: " + Arrays.toString(beatList.toArray()) + "\n\n");

        for(Heartbeat h : beatList) {
            QrsComplex qrs = h.getQrs();
            int rPeak = qrs.getRPosition();
            LOGGER.info("\n\nHeartbeat: " + h.toString() + "\nQRS: " + qrs.toString() + "\nR-peak: " + rPeak + "\n\n");
        }
    }
}
