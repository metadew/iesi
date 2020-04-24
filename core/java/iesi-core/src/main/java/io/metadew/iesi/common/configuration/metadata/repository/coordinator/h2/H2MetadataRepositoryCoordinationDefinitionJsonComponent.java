package io.metadew.iesi.common.configuration.metadata.repository.coordinator.h2;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.MetadataRepositoryCoordinationDefinitionJsonComponent;

import java.io.IOException;

public class H2MetadataRepositoryCoordinationDefinitionJsonComponent {

    public enum Field {
        TYPE("h2"),
        HOST("host"),
        PORT("port"),
        FILE("file"),
        SCHEMA("schema"),
        MODE("mode"),
        DATABASE("database");

        private final String label;

        Field(String label) {
            this.label = label;
        }

        public String value() {
            return label;
        }
    }

    public static class Deserializer extends JsonDeserializer<H2MetadataRepositoryCoordinatorDefinition> {

        @Override
        public H2MetadataRepositoryCoordinatorDefinition deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            H2MetadataRepositoryCoordinatorDefinition repositoryCoordinatorDefinition = new H2MetadataRepositoryCoordinatorDefinition();
            MetadataRepositoryCoordinationDefinitionJsonComponent.setDefaultInformation(repositoryCoordinatorDefinition, node, jsonParser);

            // Mandatory Parameters
            repositoryCoordinatorDefinition.setMode(node.get(Field.MODE.value()).asText());

            // Parameters based on mode
            switch (node.get(Field.MODE.value()).asText()) {
                case "embedded":
                    repositoryCoordinatorDefinition.setFile(node.get(Field.FILE.value()).asText());
                    break;
                case "server":
                    repositoryCoordinatorDefinition.setHost(node.get(Field.HOST.value()).asText());
                    repositoryCoordinatorDefinition.setPort(Integer.parseInt(node.get(Field.PORT.value()).asText()));
                    repositoryCoordinatorDefinition.setFile(node.get(Field.FILE.value()).asText());
                    break;
                case "memory":
                    repositoryCoordinatorDefinition.setDatabaseName(node.get(Field.DATABASE.value()).asText());
                    break;
                default:
                    break;
            }

            // Optional
            if (node.hasNonNull(Field.SCHEMA.value())) {
                repositoryCoordinatorDefinition.setSchema(node.get(Field.SCHEMA.value()).asText());;
            }
            return repositoryCoordinatorDefinition;
        }
    }




}
