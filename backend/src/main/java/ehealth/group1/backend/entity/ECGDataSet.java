package ehealth.group1.backend.entity;

import java.util.ArrayList;

public class ECGDataSet {
    ArrayList<ECGData> datasets = new ArrayList<>();

    public void addToDatasets(ECGData dataset) {
        datasets.add(dataset);
    }

    public String getJSONRepresentation() {
        StringBuilder result = new StringBuilder();

        result.append("{\"datasets\":");

        for(ECGData d : datasets) {
            result.append(d.getJSONRepresentation(1));
        }

        result.append("}");

        return result.toString();
    }
}
