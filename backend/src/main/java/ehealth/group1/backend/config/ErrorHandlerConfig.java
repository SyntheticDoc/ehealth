package ehealth.group1.backend.config;

import ehealth.group1.backend.helper.ErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.invoke.MethodHandles;

/**
 * The `ErrorHandlerConfig` class is a configuration class responsible for defining a bean that provides an
 * instance of the `ErrorHandler`. This error handler is used for handling exceptions and errors that occur
 * during the application's execution. The `ErrorHandler` bean is initialized with the application context
 * provided as a parameter to its constructor.
 */
@Configuration
public class ErrorHandlerConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private ErrorHandler errorHandler = null;

    @Bean
    public ErrorHandler ErrorHandler(ApplicationContext appctx) {
        return new ErrorHandler(appctx);
    }
}
