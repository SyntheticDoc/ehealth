package ehealth.group1.backend.entity;

import ehealth.group1.backend.enums.ECGSTATE;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class ECGAnalysisResult {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // Global result state of the ecg analysed
    private ECGSTATE ecgstate;

    private LocalDateTime timestamp;

    private String comment;

    public ECGAnalysisResult(Long id, ECGSTATE ecgstate, LocalDateTime timestamp, String comment) {
        this.id = id;
        this.ecgstate = ecgstate;
        this.timestamp = timestamp;
        this.comment = comment;
    }

    public ECGAnalysisResult() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ECGSTATE getEcgstate() {
        return ecgstate;
    }

    public void setEcgstate(ECGSTATE ecgstate) {
        this.ecgstate = ecgstate;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
