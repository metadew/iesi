package io.metadew.iesi.metadata.definition.connection;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import io.metadew.iesi.metadata.definition.MetadataJsonComponent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConnectionJsonComponent {

    public enum Field {
        TYPE("connection"),
        CONNECTION_TYPE_KEY("type"),
        DESCRIPTION_KEY("description"),
        NAME_KEY("name"),
        ENVIRONMENT_KEY("environment"),
        PARAMETERS_KEY("parameters");

        private final String label;

        Field(String label) {
            this.label = label;
        }

        public String value() {
            return label;
        }
    }

    public static class Deserializer extends JsonDeserializer<Connection> {
        @Override
        public Connection deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);

            List<ConnectionParameter> connectionParameters = new ArrayList<>();
            for (JsonNode connectionParameterNode : node.get(Field.PARAMETERS_KEY.value())) {
                connectionParameters.add(new ConnectionParameter(
                        node.get(Field.NAME_KEY.value()).asText(),
                        node.get(Field.ENVIRONMENT_KEY.value()).asText(),
                        connectionParameterNode.get(ConnectionParameterJsonComponent.Field.NAME_KEY.value()).asText(),
                        connectionParameterNode.get(ConnectionParameterJsonComponent.Field.VALUE_KEY.value()).asText()));
            }

            return new Connection(node.get(Field.NAME_KEY.value()).asText(),
                    node.get(Field.CONNECTION_TYPE_KEY.value()).asText(),
                    node.get(Field.DESCRIPTION_KEY.value()).asText(),
                    node.get(Field.ENVIRONMENT_KEY.value()).asText(),
                    connectionParameters);
        }
    }

    public static class Serializer extends JsonSerializer<Connection> {
        @Override
        public void serialize(Connection connection, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField(MetadataJsonComponent.Field.TYPE_KEY.value(), Field.TYPE.value());

            jsonGenerator.writeObjectFieldStart(MetadataJsonComponent.Field.DATA_KEY.value());

            jsonGenerator.writeStringField(Field.NAME_KEY.value(), connection.getName());
            jsonGenerator.writeStringField(Field.ENVIRONMENT_KEY.value(), connection.getEnvironment());
            jsonGenerator.writeStringField(Field.DESCRIPTION_KEY.value(), connection.getDescription());
            jsonGenerator.writeStringField(Field.CONNECTION_TYPE_KEY.value(), connection.getType());
            jsonGenerator.writeArrayFieldStart(Field.PARAMETERS_KEY.value());
            for (ConnectionParameter connectionParameter : connection.getParameters()) {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField(ConnectionParameterJsonComponent.Field.NAME_KEY.value(), connectionParameter.getName());
                jsonGenerator.writeStringField(ConnectionParameterJsonComponent.Field.VALUE_KEY.value(), connectionParameter.getValue());
                jsonGenerator.writeEndObject();
            }
            jsonGenerator.writeEndArray();

            jsonGenerator.writeEndObject();
            jsonGenerator.writeEndObject();
        }
    }



}
