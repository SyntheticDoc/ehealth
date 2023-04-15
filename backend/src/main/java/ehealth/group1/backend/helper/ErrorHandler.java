package ehealth.group1.backend.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import java.lang.invoke.MethodHandles;

public class ErrorHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private ApplicationContext appctx;

    private final String div = "   ";

    public ErrorHandler(ApplicationContext appctx) {
        this.appctx = appctx;
    }

    public void handleCriticalError(String errLocation, String customErrDesc, Exception e) {
        postError("CRITICAL", errLocation, customErrDesc, e);
        shutdown(-1);
    }

    public void handleCustomException(String errLocation, String customErrDesc, Exception e) {
        postError("CustomException", errLocation, customErrDesc, e);
    }

    // Posts a critical error to the console and then shuts down server gracefully (hopefully!)
    private void postError(String errLevel, String errLocation, String customErrDesc, Exception e) {
        StringBuilder errorMsg = new StringBuilder();

        errorMsg.append(errLevel).append(" Error: ");
        errorMsg.append("In ").append(errLocation).append(": ").append(customErrDesc).append("\n");
        errorMsg.append(div).append("ERROR DETAILS:").append("\n");
        errorMsg.append(div).append(e.getMessage());

        for(StackTraceElement elem : e.getStackTrace()) {
            errorMsg.append(div).append(elem).append("\n");
        }

        LOGGER.error(errorMsg.toString());
    }

    private void shutdown(int exitCode) {
        System.exit(SpringApplication.exit(appctx, () -> exitCode));
    }
}
