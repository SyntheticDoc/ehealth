package ehealth.group1.backend.config;

import ca.uhn.fhir.context.FhirContext;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.hl7.fhir.r5.model.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This method defines a bean for the `ObjectMapper`, which is a JSON object mapper used for serialization and
 * deserialization. It configures the `ObjectMapper` with a custom `SimpleModule` to handle serialization and
 * deserialization of FHIR (Fast Healthcare Interoperability Resources) resources using the `FhirHapiSerializer`
 * and `FhirHapiDeserializer` classes. The method registers the `SimpleModule` with the `ObjectMapper` and returns
 * the configured `ObjectMapper` instance.
 */
@Configuration
public class SerializationConfig {

    @Bean
    public ObjectMapper getObjectMapper(FhirContext ctx) {
        ObjectMapper mapper = new ObjectMapper();

        SimpleModule simpleModule = new SimpleModule();

        simpleModule.addSerializer(Resource.class, new FhirHapiSerializer<>(Resource.class, ctx));

        simpleModule.setDeserializerModifier(
                new BeanDeserializerModifier() {
                    @SuppressWarnings({"unchecked", "rawtypes"})
                    @Override
                    public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc,
                                                                  JsonDeserializer<?> deserializer) {
                        if(Resource.class.isAssignableFrom(beanDesc.getBeanClass())) {
                            return new FhirHapiDeserializer(beanDesc.getBeanClass(), ctx);
                        }

                        return deserializer;
                    }
                });

        mapper.registerModule(simpleModule);
        mapper.findAndRegisterModules();

        return mapper;
    }
}
