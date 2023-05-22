package ehealth.group1.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for transmitting ecg data to and from the database. Uses ECGDataComponent to represent each component (lead) of
 * the ecg.
 */
@Entity
@NoArgsConstructor
@Getter @Setter
@ToString
public class ECGData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private LocalDateTime timestamp;

    @Transient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm:ss:SSS");

    @ManyToOne
    private ECGDeviceComponent component;

    @Column(length = 262144)
    private String data;

    @ManyToOne
    private ECGAnalysisResult ecgAnalysisResult;

    public ECGData(LocalDateTime timestamp, ECGDeviceComponent component, ECGAnalysisResult ecgAnalysisResult,
                   String data) {
        this.timestamp = timestamp;
        this.component = component;
        this.ecgAnalysisResult = ecgAnalysisResult;
        this.data = data;
    }
}
