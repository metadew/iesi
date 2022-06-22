package io.metadew.iesi.common.configuration.metadata.repository.coordinator.mysql;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.MetadataRepositoryCoordinationDefinitionJsonComponent;

import java.io.IOException;

public class MysqlMetadataRepositoryCoordinationDefinitionJsonComponent {

    public enum Field {
        TYPE("mysql"),
        HOST("host"),
        PORT("port"),
        SCHEMA("schema");

        private final String label;

        Field(String label) {
            this.label = label;
        }

        public String value() {
            return label;
        }
    }

    public static class Deserializer extends JsonDeserializer<MysqlMetadataRepositoryCoordinatorDefinition> {

        @Override
        public MysqlMetadataRepositoryCoordinatorDefinition deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            MysqlMetadataRepositoryCoordinatorDefinition repositoryCoordinatorDefinition = new MysqlMetadataRepositoryCoordinatorDefinition();
            MetadataRepositoryCoordinationDefinitionJsonComponent.setDefaultInformation(repositoryCoordinatorDefinition, node, jsonParser);
            repositoryCoordinatorDefinition.setHost(node.get(Field.HOST.value()).asText());
            repositoryCoordinatorDefinition.setPort(Integer.parseInt(node.get(Field.PORT.value()).asText()));
            repositoryCoordinatorDefinition.setSchema(node.get(Field.SCHEMA.value()).asText());

            return repositoryCoordinatorDefinition;
        }
    }


}
