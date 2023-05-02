package ehealth.group1.backend.entity;

/**
 *  Container class to report the analysis result of the most recent analysed ecg to the frontend
 */
public class ECGHealthStatus {
    String associatedUserName;
    ECGAnalysisResult lastAnalysisResult;

    public ECGHealthStatus(String associatedUserName, ECGAnalysisResult lastAnalysisResult) {
        this.associatedUserName = associatedUserName;
        this.lastAnalysisResult = lastAnalysisResult;
    }

    public ECGHealthStatus() {

    }

    public String getAssociatedUserName() {
        return associatedUserName;
    }

    public void setAssociatedUserName(String associatedUserName) {
        this.associatedUserName = associatedUserName;
    }

    public ECGAnalysisResult getLastAnalysisResult() {
        return lastAnalysisResult;
    }

    public void setLastAnalysisResult(ECGAnalysisResult lastAnalysisResult) {
        this.lastAnalysisResult = lastAnalysisResult;
    }
}
