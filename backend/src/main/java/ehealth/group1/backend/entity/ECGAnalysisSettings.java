package ehealth.group1.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@NoArgsConstructor
@Getter @Setter
@ToString
public class ECGAnalysisSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    Long user_id;

    double maxDeviation;

    double maxDeviationNum;

    public ECGAnalysisSettings(Long user_id, double maxDeviation, double maxDeviationNum) {
        this.user_id = user_id;
        this.maxDeviation = maxDeviation;
        this.maxDeviationNum = maxDeviationNum;
    }
}
