package io.metadew.iesi.framework.configuration.metadata.repository.coordinator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class PostgresqlMetadataRepositoryCoordinationDefinitionJsonComponent {

    public enum Field {
        TYPE("postgresql"),
        HOST("host"),
        PORT("port"),
        NAME("name"),
        SCHEMA("schema");

        private final String label;

        Field(String label) {
            this.label = label;
        }

        public String value() {
            return label;
        }
    }

    public static class Deserializer extends JsonDeserializer<PostgresqlRepositoryCoordinatorDefinition> {

        @Override
        public PostgresqlRepositoryCoordinatorDefinition deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            PostgresqlRepositoryCoordinatorDefinition repositoryCoordinatorDefinition = new PostgresqlRepositoryCoordinatorDefinition();
            MetadataRepositoryCoordinationDefinitionJsonComponent.setDefaultInformation(repositoryCoordinatorDefinition, node, jsonParser);
            repositoryCoordinatorDefinition.setHost(node.get(Field.HOST.value()).asText());
            repositoryCoordinatorDefinition.setPort(node.get(Field.PORT.value()).asText());
            repositoryCoordinatorDefinition.setName(node.get(Field.NAME.value()).asText());
            repositoryCoordinatorDefinition.setSchema(node.get(Field.SCHEMA.value()).asText());
            return repositoryCoordinatorDefinition;
        }
    }


}
