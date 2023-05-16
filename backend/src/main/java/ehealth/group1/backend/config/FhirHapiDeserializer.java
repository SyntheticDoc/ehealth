package ehealth.group1.backend.config;

import ca.uhn.fhir.context.FhirContext;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.Serial;

/**
 * This method overrides the `deserialize` method of the `StdDeserializer` class to provide custom deserialization
 * logic for FHIR (Fast Healthcare Interoperability Resources) resources using HAPI FHIR. It reads JSON data from
 * the `JsonParser`, converts it into a FHIR resource tree, and parses it using the `FhirContext` to deserialize
 * it into an instance of the specified resource type.
 */

@Configuration
public class FhirHapiDeserializer<K extends IBaseResource> extends StdDeserializer<K> {
    @Serial
    private static final long serialVersionUID = 494894564L;

    private final FhirContext ctx;

    public FhirHapiDeserializer(final Class<K> classType, final FhirContext ctx) {
        super(classType);
        this.ctx = ctx;
    }

    public FhirHapiDeserializer() {
        this(null, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public K deserialize(final JsonParser jsonParser, final DeserializationContext context)
        throws IOException, JsonProcessingException {
        final String fhirResourceTree = jsonParser.getCodec().readTree(jsonParser).toString();
        return ctx.newJsonParser().parseResource((Class<K>) super.handledType(), fhirResourceTree);
    }
}
