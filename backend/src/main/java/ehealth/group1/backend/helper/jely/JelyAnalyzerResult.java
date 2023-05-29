package ehealth.group1.backend.helper.jely;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
public class JelyAnalyzerResult {
    boolean isWarning = false;
    String result;

    @Override
    public String toString() {
        return "JelyAnalyzerResult:\nisWarning? " + isWarning + "\n" + result;
    }
}
