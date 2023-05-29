package ehealth.group1.backend.helper;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@Getter @Setter
public class TransientServerSettings {
    @Getter(AccessLevel.NONE)
    private boolean writeDataToDisk;
    @Getter(AccessLevel.NONE)
    private boolean drawEcgData;

    // constant strings for spring profiles, for easy and safe profile switching
    private final String PROFILE_PRODUCTION = "production";
    private final String PROFILE_DEVELOPMENT = "dev";
    private final String PROFILE_TESTING = "test";
    private final String PROFILE_NOSEC = "nosec";

    public boolean writeDataToDisk() {
        return writeDataToDisk;
    }

    public boolean drawEcgData() {
        return drawEcgData;
    }

    public String toString() {
        return "CURRENT SERVER SETTINGS:\n" +
                String.format("%20s : %s\n", "writeDataToDisk", writeDataToDisk) +
                String.format("%20s : %s", "drawEcgData", drawEcgData);
    }
}
