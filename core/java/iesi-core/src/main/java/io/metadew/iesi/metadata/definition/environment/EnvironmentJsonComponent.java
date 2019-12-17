package io.metadew.iesi.metadata.definition.environment;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import io.metadew.iesi.metadata.definition.MetadataJsonComponent;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentParameterKey;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EnvironmentJsonComponent {

    public enum Field {
        TYPE("environment"),
        NAME_KEY("name"),
        DESCRIPTION_KEY("description"),
        PARAMETERS_KEY("parameters");

        private final String label;

        Field(String label) {
            this.label = label;
        }

        public String value() {
            return label;
        }
    }

    public static class Deserializer extends JsonDeserializer<Environment> {
        @Override
        public Environment deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            String environmentName = node.get(Field.NAME_KEY.value()).asText();

            List<EnvironmentParameter> environmentParameters = new ArrayList<>();
            for (JsonNode environmentParameterNode : node.get(Field.PARAMETERS_KEY.value())) {
                environmentParameters.add(new EnvironmentParameter(new EnvironmentParameterKey(
                        environmentName,
                        environmentParameterNode.get(EnvironmentParameterJsonComponent.Field.NAME_KEY.value()).asText()),
                        environmentParameterNode.get(EnvironmentParameterJsonComponent.Field.VALUE_KEY.value()).asText()));
            }

            return new Environment(environmentName,
                    node.get(Field.DESCRIPTION_KEY.value()).asText(),
                    environmentParameters);
        }
    }

    public static class Serializer extends JsonSerializer<Environment> {
        @Override
        public void serialize(Environment environment, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField(MetadataJsonComponent.Field.TYPE_KEY.value(), Field.TYPE.value());

            jsonGenerator.writeObjectFieldStart(MetadataJsonComponent.Field.DATA_KEY.value());

            jsonGenerator.writeStringField(Field.NAME_KEY.value(), environment.getName());
            jsonGenerator.writeStringField(Field.DESCRIPTION_KEY.value(), environment.getDescription());
            jsonGenerator.writeArrayFieldStart(Field.PARAMETERS_KEY.value());
            for (EnvironmentParameter environmentParameter : environment.getParameters()) {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField(EnvironmentParameterJsonComponent.Field.NAME_KEY.value(), environmentParameter.getName());
                jsonGenerator.writeStringField(EnvironmentParameterJsonComponent.Field.VALUE_KEY.value(), environmentParameter.getValue());
                jsonGenerator.writeEndObject();
            }
            jsonGenerator.writeEndArray();

            jsonGenerator.writeEndObject();
            jsonGenerator.writeEndObject();
        }
    }
}
