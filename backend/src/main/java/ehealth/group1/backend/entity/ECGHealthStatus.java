package ehealth.group1.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *  Container class to report the analysis result of the most recent analysed ecg to the frontend
 */
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class ECGHealthStatus {
    String associatedUserName;
    ECGAnalysisResult lastAnalysisResult;
}
