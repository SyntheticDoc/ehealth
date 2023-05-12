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
