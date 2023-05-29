package ehealth.group1.backend.customfhirstructures;

import ca.uhn.fhir.model.api.annotation.*;
import ca.uhn.fhir.model.api.annotation.Extension;
import org.hl7.fhir.r5.model.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

@ResourceDef(name="Observation", profile="http://example.com/StructureDefinition/customobservation")
public class CustomObservation extends Observation {
    private static final long serialVersionUID = 1L;

    /**
     * Each extension is defined in a field. Any valid HAPI Data Type
     * can be used for the field type. Note that the [name=""] attribute
     * in the @Child annotation needs to match the name for the bean accessor
     * and mutator methods.
     */
    @Child(name="checksum")
    @Extension(url="http://example.com/dontuse#checksum", definedLocally=false, isModifier=false)
    @Description(shortDefinition="Checksum for checking observation data integrity")
    private IntegerType checksum;


    /**
     * Extension for timestamp of observation
     */
    @Child(name="timestamp")
    @Extension(url="http://example.com/dontuse#timestamp", definedLocally=false, isModifier=false)
    @Description(shortDefinition="Timestamp for this observation")
    private DateTimeType timestamp;

    /**
     * Extension for device id of the device this observation was initially generated from
     */
    @Child(name="generatingDeviceID")
    @Extension(url="http://example.com/dontuse#generatingDeviceID", definedLocally=false, isModifier=false)
    @Description(shortDefinition="ID for the device this observation was initially generated from")
    private StringType deviceID;

    /********
     * Accessors and mutators follow
     *
     * IMPORTANT:
     * Each extension is required to have an getter/accessor and a setter/mutator.
     * You are highly recommended to create getters which create instances if they
     * do not already exist, since this is how the rest of the HAPI FHIR API works.
     ********/

    /** Getter for timestamp */
    public DateTimeType getTimestamp() {
        return timestamp;
    }

    /** Custom getter for timestamp to automatically convert to LocalDateTime */
    public LocalDateTime getTimestampAsLocalDateTime() {
        return timestamp.getValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /** Getter for checksum */
    public IntegerType getChecksum() {
        return checksum;
    }

    /** Getter for deviceID */
    public StringType getDeviceID() {
        return deviceID;
    }

    /** Setter for timestamp */
    public void setTimestamp(DateTimeType timestamp) {
        this.timestamp = timestamp;
    }

    /** Custom setter for timestamp to get FHIRs DateTimeType from supplied java LocalDateTime */
    public void setTimestampFromLocalDateTime(LocalDateTime timestamp) {
        this.timestamp = new DateTimeType(String.valueOf(timestamp));
    }

    /** Setter for checksum */
    public void setChecksum(IntegerType checksum) {
        this.checksum = checksum;
    }

    /** Custom setter for checksum to get FHIRs IntegerType from supplied java int */
    public void setChecksumFromInt(int checksum) {
        this.checksum = new IntegerType(checksum);
    }

    /** Setter for deviceID */
    public void setDeviceID(StringType deviceID) {
        this.deviceID = deviceID;
    }

    /** Custom setter for deviceID to get FHIRs StringType from supplied java String */
    public void setDeviceIDfromString(String deviceID) {
        this.deviceID = new StringType(deviceID);
    }
}
