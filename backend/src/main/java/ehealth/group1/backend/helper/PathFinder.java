package ehealth.group1.backend.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;

@Component
public class PathFinder {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final static Resource dataOutPath = new ClassPathResource("data.out");
    private final static Resource customObservationTemplatePath = new ClassPathResource("JSONTemplates/CustomObservationTemplate.json");

    public static File getPath(String target) throws IllegalArgumentException, IOException {
        File result;

        switch(target.toLowerCase()) {
            case "data.out":
                result = dataOutPath.getFile();
                break;
            case "customobservationtemplate":
                result = customObservationTemplatePath.getFile();
                break;
            default:
                throw new IllegalArgumentException("PathFinder.getPath(): Can't find path target " + target);
        }

        result = new File(result.getAbsolutePath().replace("target\\classes", "src\\main\\resources"));

        return result;
    }
}
