package ehealth.group1.backend.helper.jely;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClassifiedBeatList {
    HashMap<String, Integer> beats = new HashMap<>();

    public void addBeat(String explanation) {
        if(!beats.containsKey(explanation)) {
            beats.put(explanation, 1);
        } else {
            int curNum = beats.get(explanation) + 1;
            beats.put(explanation, curNum);
        }
    }

    public ArrayList<String> getResult() {
        ArrayList<String> result = new ArrayList<>();

        for(Map.Entry<String, Integer> e : beats.entrySet()) {
            result.add(e.getKey() + ": " + e.getValue());
        }

        return result;
    }
}
