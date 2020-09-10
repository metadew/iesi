package io.metadew.iesi.common.configuration.metadata.repository.coordinator.sqlite;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.MetadataRepositoryCoordinationDefinitionJsonComponent;

import java.io.IOException;

public class SqliteMetadataRepositoryCoordinationDefinitionJsonComponent {

    public enum Field {
        TYPE("sqlite"),
        FILE_KEY("file");

        private final String label;

        Field(String label) {
            this.label = label;
        }

        public String value() {
            return label;
        }
    }

    public static class Deserializer extends JsonDeserializer<SQLiteMetadataRepositoryCoordinatorDefinition> {

        @Override
        public SQLiteMetadataRepositoryCoordinatorDefinition deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            SQLiteMetadataRepositoryCoordinatorDefinition sqLiteRepositoryCoordinatorDefinition = new SQLiteMetadataRepositoryCoordinatorDefinition();
            MetadataRepositoryCoordinationDefinitionJsonComponent.setDefaultInformation(sqLiteRepositoryCoordinatorDefinition, node, jsonParser);
            sqLiteRepositoryCoordinatorDefinition.setFile(node.get(Field.FILE_KEY.value()).asText());
            return sqLiteRepositoryCoordinatorDefinition;
        }
    }


}
