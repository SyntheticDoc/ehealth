package ehealth.group1.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Entity
@NoArgsConstructor
@Getter @Setter
public class ECGDataComponent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String displayName;

    // SampledData
    private Double originValue;
    private Double intervalValue;
    private String intervalUnit;
    private Double factor;
    private Double lowerLimit;
    private Double upperLimit;
    private Integer dimensions;

    @Transient
    private String data;

    public ECGDataComponent(Long id, String displayName, String data) {
        this.id = id;
        this.displayName = displayName;
        this.data = data;
    }

    public ArrayList<String> getJSONRepresentation() {
        ArrayList<String> result = new ArrayList<>();
        String spacer = "  ";

        result.add("\"component\" : [{");
        result.add(spacer + "\"code\" : {");
        result.add(spacer + spacer + "\"coding\" : [{");
        result.add(spacer + spacer + spacer + "\"display\" : \"" + displayName + "\"");
        result.add(spacer + spacer + "}]");
        result.add(spacer + "},");
        result.add(spacer + "\"valueSampledData\" : {");
        result.add(spacer + spacer + "\"origin\" : {");
        result.add(spacer + spacer + spacer + "\"value\" : " + originValue);
        result.add(spacer + spacer + "},");
        result.add(spacer + spacer + "\"interval\" : " + intervalValue + ",");
        result.add(spacer + spacer + "\"intervalUnit\" : \"" + intervalUnit + "\",");
        result.add(spacer + spacer + "\"factor\" : " + factor + ",");
        result.add(spacer + spacer + "\"lowerLimit\" : " + lowerLimit + ",");
        result.add(spacer + spacer + "\"upperLimit\" : " + upperLimit + ",");
        result.add(spacer + spacer + "\"dimensions\" : " + dimensions + ",");
        result.add(spacer + spacer + "\"data\" : \"" + data + "\"");
        result.add(spacer + "}");
        result.add("}]");

        return result;
    }

    public String toStringShort() {
        return "ECGDataComponent[id=" + id + "]";
    }

    @Override
    public String toString() {
        return "ECGDataComponent[" +
                "id=" + id +
                ", displayName='" + displayName +
                ", originValue=" + originValue +
                ", interval=" + intervalValue +
                ", intervalUnit='" + intervalUnit +
                ", factor=" + factor +
                ", lowerLimit=" + lowerLimit +
                ", upperLimit=" + upperLimit +
                ", dimensions=" + dimensions +
                ", data='" + data +
                ']';
    }
}
