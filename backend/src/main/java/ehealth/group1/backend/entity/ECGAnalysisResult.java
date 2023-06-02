package ehealth.group1.backend.entity;

import ehealth.group1.backend.enums.ECGSTATE;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter @Setter
@ToString
public class ECGAnalysisResult {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // Global result state of the ecg analysed
    private ECGSTATE ecgstate;

    private String timestamp;

    @Column(length = 262144)
    private String comment;

    public ECGAnalysisResult(ECGSTATE ecgstate, String timestamp, String comment) {
        this.ecgstate = ecgstate;
        this.timestamp = timestamp;
        this.comment = comment;
    }
}
