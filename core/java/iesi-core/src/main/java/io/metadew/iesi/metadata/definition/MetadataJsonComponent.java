package io.metadew.iesi.metadata.definition;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.metadew.iesi.metadata.definition.connection.Connection;

import java.io.IOException;
import java.text.MessageFormat;

public class MetadataJsonComponent {

    public enum Field {
        TYPE_KEY("type"),
        DATA_KEY("data"),
        CONNECTION_TYPE_KEY("connection");

        private final String label;

        Field(String label) {
            this.label = label;
        }

        public String value() {
            return label;
        }
    }

    public static class Deserializer extends JsonDeserializer<Metadata> {

        @Override
        public Metadata deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            String type = node.get(Field.TYPE_KEY.value()).asText();
            JsonNode data = node.get(Field.DATA_KEY.value());
            if (type.equalsIgnoreCase(Field.CONNECTION_TYPE_KEY.value())) {
                return jsonParser.getCodec().treeToValue(data, Connection.class);
            } else {
                throw JsonMappingException.from(jsonParser, MessageFormat.format("Cannot deserialize Metadata object of type {0}", type));
            }
        }
    }


}
