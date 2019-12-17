package io.metadew.iesi.metadata.definition;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.component.ComponentJsonComponent;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionJsonComponent;
import io.metadew.iesi.metadata.definition.environment.EnvironmentJsonComponent;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.ScriptJsonComponent;

import java.io.IOException;
import java.text.MessageFormat;

public class MetadataJsonComponent {

    public enum Field {
        TYPE_KEY("type"),
        DATA_KEY("data");

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
            if (type.equalsIgnoreCase(ConnectionJsonComponent.Field.TYPE.value())) {
                return jsonParser.getCodec().treeToValue(data, Connection.class);
            } else if (type.equalsIgnoreCase(ScriptJsonComponent.Field.TYPE.value())) {
                return jsonParser.getCodec().treeToValue(data, Script.class);
            } else if (type.equalsIgnoreCase(ComponentJsonComponent.Field.TYPE.value())) {
                return jsonParser.getCodec().treeToValue(data, Component.class);
            } else if (type.equalsIgnoreCase(EnvironmentJsonComponent.Field.TYPE.value())) {
                return jsonParser.getCodec().treeToValue(data, Component.class);
            } else {
                throw JsonMappingException.from(jsonParser, MessageFormat.format("Cannot deserialize Metadata object of type {0}", type));
            }
        }
    }


}
