package io.metadew.iesi.server.rest.dataset.implementation;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationType;

import io.metadew.iesi.server.rest.dataset.implementation.inmemory.DatabaseDatasetImplementationPostDto;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class DatasetImplementationJsonComponent {

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

    public static class ScreeningDeserializer extends JsonDeserializer<DatasetImplementationPostDto> {

        @Override
        public DatasetImplementationPostDto deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            if (!node.hasNonNull(Field.TYPE.value())) {
                throw JsonMappingException.from(jsonParser, String.format("Dataset implementation %s must contain a type", node.toString()));
            }
            String type = node.get(Field.TYPE.value()).asText();

            if (type.equals(DatasetImplementationType.DATABASE.value())) {
                return jsonParser.getCodec().treeToValue(node, DatabaseDatasetImplementationPostDto.class);
            } else {
                throw new JsonParseException(jsonParser, "Unexpected dataset implementation type: " + jsonParser.getCodec().readTree(jsonParser).toString());
            }
        }
    }
}
