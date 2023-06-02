package ehealth.group1.backend.helper.jely;

import ehealth.group1.backend.jely.JELYmaster.de.fau.mad.jely.*;
import ehealth.group1.backend.jely.JELYmaster.de.fau.mad.jely.classifiers.GradlDecisionTreeClassifier;
import ehealth.group1.backend.jely.JELYmaster.de.fau.mad.jely.classifiers.LeutheuserC45Classifier;
import ehealth.group1.backend.jely.JELYmaster.de.fau.mad.jely.classifiers.PhysiologicalClassifier;
import ehealth.group1.backend.jely.JELYmaster.de.fau.mad.jely.classifiers.TsipourasRuleBasedClassifier;
import ehealth.group1.backend.jely.JELYmaster.de.fau.mad.jely.detectors.HeartbeatDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

@Component
public class JelyAnalyzer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public JelyAnalyzerResult analyze(double[] ecgData, double interval) {
        LOGGER.info("Starting JelyAnalyzer...");
        Ecglib.setDebugMode(false);
        LOGGER.debug("EcgLib.isDebugMode? " + Ecglib.isDebugMode());

        int samplingRate = (int) (1000 / interval);
        LOGGER.debug(String.format("Interval: %.2f, reconstructed sampling rate: %d Hz", interval, samplingRate));

        Ecg ecgFile = new Ecg(ecgData, samplingRate, EcgLead.UNKNOWN);

        //LOGGER.info("\n\necgFile: " + Arrays.toString(ecgFile.getSignal(0).getSignal().toArray()) + "\n\n");
        HeartbeatDetector detector = new HeartbeatDetector(ecgFile, (HeartbeatDetector.HeartbeatDetectionListener) null);
        ArrayList<Heartbeat> beatList = detector.findHeartbeats();

        //LOGGER.info("\n\nBeatlist: " + Arrays.toString(beatList.toArray()) + "\n\n");
        LOGGER.debug("Heartbeats found: " + beatList.size());

        if(beatList.size() <= 0) {
            LOGGER.warn("JELY couldn't find any heartbeats in the current data set. Skipping JELY analysis.");
            return new JelyAnalyzerResult(false,"JELY couldn't find any heartbeats in the current data set. Skipping JELY analysis.");
        }

        int qrsComplexNum = 0;

        for(Heartbeat b : beatList) {
            if(b.getQrs() != null) {
                qrsComplexNum++;
            }
        }

        LOGGER.info("QRS complexes found: " + qrsComplexNum);

//        for(Heartbeat h : beatList) {
//            QrsComplex qrs = h.getQrs();
//            int rPeak = qrs.getRPosition();
//            //LOGGER.info("\n\nHeartbeat: " + h.toString() + "\nQRS: " + qrs.toString() + "\nR-peak: " + rPeak + "\n\n");
//        }

        PhysiologicalClassifier classifierPhysiological = new PhysiologicalClassifier();
        GradlDecisionTreeClassifier classifierGradl = new GradlDecisionTreeClassifier();
        //LeutheuserC45Classifier classifierLeutheuser = new LeutheuserC45Classifier(null, null);
        TsipourasRuleBasedClassifier classifierTsipouras = new TsipourasRuleBasedClassifier();

        ArrayList<BeatClass> bclassesPhysiological = new ArrayList<>();
        ArrayList<GradlDecisionTreeClassifier.GradlClass> bclassesGradl = new ArrayList<>();
        //ArrayList<BeatClass> bclassesLeutheuser = new ArrayList<>();
        ArrayList<TsipourasRuleBasedClassifier.TsipourasClass> bclassesTsipouras = new ArrayList<>();

        ArrayList<QrsComplex> gradlQRSComplexes = new ArrayList<>();
        ArrayList<QrsComplex> tsipourasQRSComplexes = new ArrayList<>();

        for(Heartbeat h : beatList) {
            bclassesPhysiological.add(classifierPhysiological.classify(h));
            gradlQRSComplexes.add(h.getQrs());
            //bclassesLeutheuser.add(classifierLeutheuser.classify(h));
            tsipourasQRSComplexes.add(h.getQrs());
        }

        bclassesGradl.addAll(classifierGradl.classifyToGradlClass(gradlQRSComplexes));
        bclassesTsipouras.addAll(classifierTsipouras.classifyToTsipourasClass(tsipourasQRSComplexes));

        ClassifiedBeatList physiologicalList = new ClassifiedBeatList();
        ClassifiedBeatList gradlList = new ClassifiedBeatList();
        ClassifiedBeatList tsipourasList = new ClassifiedBeatList();

        boolean hasJelyWarning = false;

        for (BeatClass bclass : bclassesPhysiological) {
            if (bclass != null && bclass.isAbnormal()) {
                physiologicalList.addBeat(bclass.getExplanation());
            } else {
                physiologicalList.addBeat("Normal beat");
            }
        }

        for (GradlDecisionTreeClassifier.GradlClass bclass : bclassesGradl) {
            if (bclass != null && bclass != GradlDecisionTreeClassifier.GradlClass.NORMAL &&
                    bclass != GradlDecisionTreeClassifier.GradlClass.UNKNOWN &&
                    bclass != GradlDecisionTreeClassifier.GradlClass.ARTIFACT) {
                gradlList.addBeat(bclass.name());
                hasJelyWarning = true;
            } else if(bclass == GradlDecisionTreeClassifier.GradlClass.UNKNOWN) {
                gradlList.addBeat(bclass.name());
            } else if(bclass == GradlDecisionTreeClassifier.GradlClass.ARTIFACT) {
                gradlList.addBeat(bclass.name() + "(NORMAL)");
            } else {
                gradlList.addBeat("NORMAL");
            }
        }

        for (TsipourasRuleBasedClassifier.TsipourasClass bclass : bclassesTsipouras) {
            if (bclass != null && bclass != TsipourasRuleBasedClassifier.TsipourasClass.NORMAL && bclass != TsipourasRuleBasedClassifier.TsipourasClass.UNKNOWN) {
                tsipourasList.addBeat(bclass.name());
                hasJelyWarning = true;
            } else if(bclass == TsipourasRuleBasedClassifier.TsipourasClass.UNKNOWN) {
                gradlList.addBeat(bclass.name());
            } else {
                tsipourasList.addBeat("NORMAL");
            }
        }

        ArrayList<String> classResultsPhysiological = physiologicalList.getResult();
        ArrayList<String> classResultsGradl = gradlList.getResult();
        ArrayList<String> classResultsTsipouras = tsipourasList.getResult();

        StringBuilder result = new StringBuilder();

        Collections.sort(classResultsPhysiological);
        Collections.sort(classResultsGradl);
        Collections.sort(classResultsTsipouras);

        result.append("JELY ANALYZER RESULTS").append("\n").append("Physiological classifier:\n");

        for(String s : classResultsPhysiological) {
            result.append("   ").append(s).append("\n");
        }

        result.append("Gradl decision tree classifier:\n");

        for(String s : classResultsGradl) {
            result.append("   ").append(s).append("\n");
        }

        result.append("Tsipouras rule based classifier:\n");

        for(String s : classResultsTsipouras) {
            result.append("   ").append(s).append("\n");
        }

        result.append("END OF JELY RESULTS");

        JelyAnalyzerResult jelyResult = new JelyAnalyzerResult(hasJelyWarning, result.toString());

        LOGGER.info(jelyResult.toString());

        return jelyResult;
    }
}
