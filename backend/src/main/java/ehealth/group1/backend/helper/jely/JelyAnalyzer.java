package ehealth.group1.backend.helper.jely;

import ehealth.group1.backend.jely.JELYmaster.de.fau.mad.jely.*;
import ehealth.group1.backend.jely.JELYmaster.de.fau.mad.jely.classifiers.PhysiologicalClassifier;
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

        Ecg ecgFile = new Ecg(ecgData, 500.0, EcgLead.UNKNOWN);

        //LOGGER.info("\n\necgFile: " + Arrays.toString(ecgFile.getSignal(0).getSignal().toArray()) + "\n\n");
        HeartbeatDetector detector = new HeartbeatDetector(ecgFile, (HeartbeatDetector.HeartbeatDetectionListener) null);
        ArrayList<Heartbeat> beatList = detector.findHeartbeats();

        //LOGGER.info("\n\nBeatlist: " + Arrays.toString(beatList.toArray()) + "\n\n");
        LOGGER.info("Heartbeats found: " + beatList.size());

        for(Heartbeat h : beatList) {
            QrsComplex qrs = h.getQrs();
            int rPeak = qrs.getRPosition();
            //LOGGER.info("\n\nHeartbeat: " + h.toString() + "\nQRS: " + qrs.toString() + "\nR-peak: " + rPeak + "\n\n");
        }

        PhysiologicalClassifier classifier = new PhysiologicalClassifier();

        StringBuilder results = new StringBuilder();

        ArrayList<BeatClass> bclasses = new ArrayList<>();

        for(Heartbeat h : beatList) {
            bclasses.add(classifier.classify(h));
        }

        for(BeatClass bclass : bclasses) {
            if(bclass.isAbnormal()) {
                results.append(bclass.toString()).append("\n");
            } else {
                results.append("NORMAL beat").append("\n");
            }
        }

        LOGGER.info("PhysiologicalClassifier results:\n" + results);
    }
}
