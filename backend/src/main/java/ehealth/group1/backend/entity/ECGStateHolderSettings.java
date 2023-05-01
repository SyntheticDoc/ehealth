package ehealth.group1.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ECGStateHolderSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    Long user_id;

    int iterationsToStateTransition;

    int iterationsToEmergencyCall;

    public ECGStateHolderSettings(Long user_id, int iterationsToStateTransition, int iterationsToEmergencyCall) {
        this.user_id = user_id;
        this.iterationsToStateTransition = iterationsToStateTransition;
        this.iterationsToEmergencyCall = iterationsToEmergencyCall;
    }

    public ECGStateHolderSettings() {
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

    public int getIterationsToStateTransition() {
        return iterationsToStateTransition;
    }

    public void setIterationsToStateTransition(int iterationsToStateTransition) {
        this.iterationsToStateTransition = iterationsToStateTransition;
    }

    public int getIterationsToEmergencyCall() {
        return iterationsToEmergencyCall;
    }

    public void setIterationsToEmergencyCall(int iterationsToEmergencyCall) {
        this.iterationsToEmergencyCall = iterationsToEmergencyCall;
    }
}
