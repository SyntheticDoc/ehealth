package ehealth.group1.backend.entity;

import jakarta.persistence.*;

import java.util.ArrayList;

@Entity
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

    public ECGDataComponent() {

    }

    public void setOriginValue(Double originValue) {
        if (this.originValue != null) {
            throw new IllegalStateException("ECGDataComponent.setOriginValue(): originValue is not null, can't set new " +
                    "new origin value, possible illegal access?");
        } else {
            this.originValue = originValue;
        }
    }

    public void setIntervalValue(Double interval) {
        if (this.intervalValue != null) {
            throw new IllegalStateException("ECGDataComponent.setInterval(): interval is not null, can't set new " +
                    "new interval value, possible illegal access?");
        } else {
            this.intervalValue = interval;
        }
    }

    public void setIntervalUnit(String intervalUnit) {
        if (this.intervalUnit != null) {
            throw new IllegalStateException("ECGDataComponent.setIntervalUnit(): intervalUnit is not null, can't set new " +
                    "new intervalUnit value, possible illegal access?");
        } else {
            this.intervalUnit = intervalUnit;
        }
    }

    public void setFactor(Double factor) {
        if (this.factor != null) {
            throw new IllegalStateException("ECGDataComponent.setFactor(): factor is not null, can't set new " +
                    "new factor value, possible illegal access?");
        } else {
            this.factor = factor;
        }
    }

    public void setLowerLimit(Double lowerLimit) {
        if (this.lowerLimit != null) {
            throw new IllegalStateException("ECGDataComponent.setLowerLimit(): lowerLimit is not null, can't set new " +
                    "new lowerLimit value, possible illegal access?");
        } else {
            this.lowerLimit = lowerLimit;
        }
    }

    public void setUpperLimit(Double upperLimit) {
        if (this.upperLimit != null) {
            throw new IllegalStateException("ECGDataComponent.setUpperLimit(): upperLimit is not null, can't set new " +
                    "new upperLimit value, possible illegal access?");
        } else {
            this.upperLimit = upperLimit;
        }
    }

    public void setDimensions(Integer dimensions) {
        if (this.dimensions != null) {
            throw new IllegalStateException("ECGDataComponent.setDimensions(): dimensions is not null, can't set new " +
                    "new dimensions value, possible illegal access?");
        } else {
            this.dimensions = dimensions;
        }
    }

    public Long getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Double getOriginValue() {
        return originValue;
    }

    public Double getIntervalValue() {
        return intervalValue;
    }

    public String getIntervalUnit() {
        return intervalUnit;
    }

    public Double getFactor() {
        return factor;
    }

    public Double getLowerLimit() {
        return lowerLimit;
    }

    public Double getUpperLimit() {
        return upperLimit;
    }

    public Integer getDimensions() {
        return dimensions;
    }

    public String getData() {
        return data;
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
