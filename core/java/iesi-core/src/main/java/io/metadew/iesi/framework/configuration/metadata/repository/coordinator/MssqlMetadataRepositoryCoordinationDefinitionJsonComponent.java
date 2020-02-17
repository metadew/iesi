package io.metadew.iesi.framework.configuration.metadata.repository.coordinator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class MssqlMetadataRepositoryCoordinationDefinitionJsonComponent {

    public enum Field {
        TYPE("mssql"),
        HOST("host"),
        PORT("port"),
        SCHEMA("schema"),
        DATABASE("database");

        private final String label;

        Field(String label) {
            this.label = label;
        }

        public String value() {
            return label;
        }
    }

    public static class Deserializer extends JsonDeserializer<MssqlRepositoryCoordinatorDefinition> {

        @Override
        public MssqlRepositoryCoordinatorDefinition deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            MssqlRepositoryCoordinatorDefinition repositoryCoordinatorDefinition = new MssqlRepositoryCoordinatorDefinition();
            MetadataRepositoryCoordinationDefinitionJsonComponent.setDefaultInformation(repositoryCoordinatorDefinition, node, jsonParser);
            repositoryCoordinatorDefinition.setHost(node.get(Field.HOST.value()).asText());
            repositoryCoordinatorDefinition.setPort(node.get(Field.PORT.value()).asText());
            repositoryCoordinatorDefinition.setDatabase(node.get(Field.DATABASE.value()).asText());
            repositoryCoordinatorDefinition.setSchema(node.get(Field.SCHEMA.value()).asText());
            return repositoryCoordinatorDefinition;
        }
    }


}
