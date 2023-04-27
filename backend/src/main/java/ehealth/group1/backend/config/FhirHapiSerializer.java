package ehealth.group1.backend.config;

import ca.uhn.fhir.context.FhirContext;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.Serial;

@Configuration
public class FhirHapiSerializer<K extends IBaseResource> extends StdSerializer<K> {
    @Serial
    private static final long serialVersionUID = 564894561894L;
    private final FhirContext ctx;

    public FhirHapiSerializer(final Class<K> classType, final FhirContext ctx) {
        super(classType);
        this.ctx = ctx;
    }

    public FhirHapiSerializer() {
        this(null, null);
    }

    @Override
    public void serialize(final K fhirResource, final JsonGenerator jsonGenerator, final SerializerProvider serializerProvider)
            throws IOException {
        final String stringifiedFhirResource = ctx.newJsonParser().encodeResourceToString(fhirResource);
        jsonGenerator.enable(JsonGenerator.Feature.IGNORE_UNKNOWN);
        jsonGenerator.writeRaw(stringifiedFhirResource);
    }
}
