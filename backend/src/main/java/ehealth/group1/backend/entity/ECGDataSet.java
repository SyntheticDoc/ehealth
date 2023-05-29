package ehealth.group1.backend.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

/**
 * Represents a dataset of multiple ECGData objects. This class is used to send all ecg data requested to the frontend.
 */
@NoArgsConstructor
@Getter
public class ECGDataSet {
    ArrayList<ECGData> datasets = new ArrayList<>();

    public void addToDatasets(ECGData dataset) {
        datasets.add(dataset);
    }
}
