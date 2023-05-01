package ehealth.group1.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ECGAnalysisSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    Long user_id;

    int maxDeviation;

    int maxDeviationNum;

    public ECGAnalysisSettings(Long user_id, int maxDeviation, int maxDeviationNum) {
        this.user_id = user_id;
        this.maxDeviation = maxDeviation;
        this.maxDeviationNum = maxDeviationNum;
    }

    public ECGAnalysisSettings() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public int getMaxDeviation() {
        return maxDeviation;
    }

    public void setMaxDeviation(int maxDeviation) {
        this.maxDeviation = maxDeviation;
    }

    public int getMaxDeviationNum() {
        return maxDeviationNum;
    }

    public void setMaxDeviationNum(int maxDeviationNum) {
        this.maxDeviationNum = maxDeviationNum;
    }
}
