package ehealth.group1.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Settings for controlling the behavior of the ECGStateHolder.
 *
 * iterationsToStateTransition controls after how many iterations with a ECGSTATE.WARNING the ECGStateHolder switches to
 * ECGSTATE.CRITICAL.
 *
 * iterationsToEmergencyCall controls after how many iterations with ECGSTATE.CRITICAL the ECGStateHolder switches to
 * ECGSTATE.CALLEMERGENCY. This is the timespan for the user to abort the emergency call.
 */
@Entity
@NoArgsConstructor
@Getter @Setter
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
}
