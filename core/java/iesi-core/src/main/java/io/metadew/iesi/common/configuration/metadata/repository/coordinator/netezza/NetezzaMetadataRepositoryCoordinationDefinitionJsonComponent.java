package io.metadew.iesi.common.configuration.metadata.repository.coordinator.netezza;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.MetadataRepositoryCoordinationDefinitionJsonComponent;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.h2.H2MetadataRepositoryCoordinationDefinitionJsonComponent;

import java.io.IOException;

public class NetezzaMetadataRepositoryCoordinationDefinitionJsonComponent {

    public enum Field {
        TYPE("netezza"),
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

    public static class Deserializer extends JsonDeserializer<NetezzaMetadataRepositoryCoordinatorDefinition> {

        @Override
        public NetezzaMetadataRepositoryCoordinatorDefinition deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            NetezzaMetadataRepositoryCoordinatorDefinition repositoryCoordinatorDefinition = new NetezzaMetadataRepositoryCoordinatorDefinition();
            MetadataRepositoryCoordinationDefinitionJsonComponent.setDefaultInformation(repositoryCoordinatorDefinition, node, jsonParser);
            // Optional
            repositoryCoordinatorDefinition.setHost(node.get(Field.HOST.value()).asText());
            repositoryCoordinatorDefinition.setPort(Integer.parseInt(node.get(Field.PORT.value()).asText()));
            repositoryCoordinatorDefinition.setName(node.get(Field.NAME.value()).asText());
            // Optional
            if (node.hasNonNull(Field.SCHEMA.value())) {
                repositoryCoordinatorDefinition.setSchema(node.get(H2MetadataRepositoryCoordinationDefinitionJsonComponent.Field.SCHEMA.value()).asText());;
            }
            return repositoryCoordinatorDefinition;
        }
    }


}
