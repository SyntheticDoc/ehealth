package ehealth.group1.backend.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ECGData {
    private final Long id;
    private final LocalDateTime timestamp;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm:ss:SSS");

    private final String deviceName;
    private final ArrayList<ECGDataComponent> components;

    public ECGData(Long id, LocalDateTime timestamp, String deviceName, ArrayList<ECGDataComponent> components) {
        this.id = id;
        this.timestamp = timestamp;
        this.deviceName = deviceName;
        this.components = components;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public ArrayList<ECGDataComponent> getComponents() {
        return components;
    }

    public String getJSONRepresentation(int startSpacerNum) {
        StringBuilder result = new StringBuilder();
        String startSpacer = "  ".repeat(startSpacerNum);
        String spacer = startSpacer + "  ";
        String spacer2 = spacer + spacer;
        String spacer3 = spacer + spacer2;

        result.append("{\"timestamp\":\"").append(timestamp.format(dtf)).append("\"}").append("\n");
        result.append("{").append("\n");
        result.append(spacer).append("\"resourceType\" : \"Observation\",").append("\n");
        result.append(spacer).append("\"id\" : \"ekg\",").append("\n");
        result.append(spacer).append("\"status\" : \"final\",").append("\n");
        result.append(spacer).append("\"category\" : [{").append("\n");
        result.append(spacer2).append("\"coding\" : [{").append("\n");
        result.append(spacer3).append("\"system\" : \"http://terminology.hl7.org/CodeSystem/observation-category\",").append("\n");
        result.append(spacer3).append("\"code\" : \"procedure\",").append("\n");
        result.append(spacer3).append("\"display\" : \"Procedure\"").append("\n");
        result.append(spacer2).append("}]").append("\n");
        result.append(spacer).append("}],").append("\n");
        result.append(spacer).append("\"device\" : {").append("\n");
        result.append(spacer2).append("\"display\" : \"").append(deviceName).append("\"\n");
        result.append(spacer).append("},").append("\n");

        for(ECGDataComponent c : components) {
            for(String s : c.getJSONRepresentation()) {
                result.append(spacer).append(s).append("\n");
            }
        }

        result.append("}");

        return result.toString();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        result.append("ECGData[id=").append(id).append(",");
        result.append(" timestamp=").append(timestamp.format(dtf)).append(",");
        result.append(" deviceName='").append(deviceName).append("',");
        result.append(" components=");

        for(int i = 0; i < components.size(); i++) {
            result.append(components.get(i).toStringShort());

            if(i < (components.size()) - 1) {
                result.append(",");
            }
        }

        result.append("]");

        return result.toString();
    }
}
