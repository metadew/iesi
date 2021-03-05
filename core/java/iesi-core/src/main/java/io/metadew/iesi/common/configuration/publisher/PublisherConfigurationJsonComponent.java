package io.metadew.iesi.common.configuration.publisher;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class PublisherConfigurationJsonComponent {


    public enum Field {
        TYPE("type");

        private final String label;

        Field(String label) {
            this.label = label;
        }

        public String value() {
            return label;
        }
    }

    public static class Deserializer extends JsonDeserializer<PublisherConfiguration> {
        @Override
        public PublisherConfiguration deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            // needs to be a predictable key (hash) to ensure they can be loaded from filesystem
            String type = node.get(Field.TYPE.value()).asText();
            if (type.equalsIgnoreCase(GCPPublisherConfigurationJsonComponent.Field.TYPE.value())) {
                return jsonParser.getCodec().treeToValue(node, GCPPublisherConfiguration.class);
            } else {
                throw new RuntimeException("Could not create Publisher configuration of type " + type);
            }
        }
    }

}
