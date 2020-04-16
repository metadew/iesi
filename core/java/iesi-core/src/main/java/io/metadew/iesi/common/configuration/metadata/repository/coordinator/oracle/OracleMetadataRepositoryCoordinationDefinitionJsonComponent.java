package io.metadew.iesi.common.configuration.metadata.repository.coordinator.oracle;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.MetadataRepositoryCoordinationDefinitionJsonComponent;

import java.io.IOException;

public class OracleMetadataRepositoryCoordinationDefinitionJsonComponent {

    public enum Field {
        TYPE("oracle"),
        MODE("mode"),
        HOST("host"),
        PORT("port"),
        SCHEMA("schema"),
        SERVICE("service"),
        TNS_ALIAS("tnsalias");

        private final String label;

        Field(String label) {
            this.label = label;
        }

        public String value() {
            return label;
        }
    }

    public static class Deserializer extends JsonDeserializer<OracleMetadataRepositoryCoordinatorDefinition> {

        @Override
        public OracleMetadataRepositoryCoordinatorDefinition deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            OracleMetadataRepositoryCoordinatorDefinition repositoryCoordinatorDefinition = new OracleMetadataRepositoryCoordinatorDefinition();
            MetadataRepositoryCoordinationDefinitionJsonComponent.setDefaultInformation(repositoryCoordinatorDefinition, node, jsonParser);
            if (!MetadataRepositoryCoordinationDefinitionJsonComponent.hasConnectionUrlSet(node)) {
                repositoryCoordinatorDefinition.setMode(node.get(Field.MODE.value()).asText());

                // Parameters based on mode
                switch (node.get(Field.MODE.value()).asText()) {
                    case "service":
                        repositoryCoordinatorDefinition.setHost(node.get(Field.HOST.value()).asText());
                        repositoryCoordinatorDefinition.setPort(Integer.parseInt(node.get(Field.PORT.value()).asText()));
                        repositoryCoordinatorDefinition.setService(node.get(Field.SERVICE.value()).asText());
                        break;
                    case "tns":
                        repositoryCoordinatorDefinition.setHost(node.get(Field.HOST.value()).asText());
                        repositoryCoordinatorDefinition.setPort(Integer.parseInt(node.get(Field.PORT.value()).asText()));
                        repositoryCoordinatorDefinition.setTnsAlias(node.get(Field.TNS_ALIAS.value()).asText());
                        break;
                    default:
                        break;
                }
            }
            // Optional
            if (node.hasNonNull(Field.SCHEMA.value())) {
                repositoryCoordinatorDefinition.setSchema(node.get(Field.SCHEMA.value()).asText());
            }
            return repositoryCoordinatorDefinition;
        }
    }


}
