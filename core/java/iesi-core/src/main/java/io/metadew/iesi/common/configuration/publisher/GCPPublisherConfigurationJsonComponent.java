package io.metadew.iesi.common.configuration.publisher;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class GCPPublisherConfigurationJsonComponent {


    public enum Field {
        TYPE("gcppubsub"),
        PROJECT("project"),
        TOPIC("topic");

        private final String label;

        Field(String label) {
            this.label = label;
        }

        public String value() {
            return label;
        }
    }

    public static class Deserializer extends JsonDeserializer<GCPPublisherConfiguration> {
        @Override
        public GCPPublisherConfiguration deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            // needs to be a predictable key (hash) to ensure they can be loaded from filesystem
            return new GCPPublisherConfiguration(node.get(Field.PROJECT.value()).asText(), node.get(Field.TOPIC.value()).asText());
        }
    }

}
