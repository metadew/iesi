package io.metadew.iesi.framework.configuration.metadata.repository.coordinator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

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

    public static class Deserializer extends JsonDeserializer<OracleRepositoryCoordinatorDefinition> {

        @Override
        public OracleRepositoryCoordinatorDefinition deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            OracleRepositoryCoordinatorDefinition repositoryCoordinatorDefinition = new OracleRepositoryCoordinatorDefinition();
            MetadataRepositoryCoordinationDefinitionJsonComponent.setDefaultInformation(repositoryCoordinatorDefinition, node, jsonParser);

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

            // Optional
            if (node.hasNonNull(Field.SCHEMA.value())) {
                repositoryCoordinatorDefinition.setSchema(node.get(Field.SCHEMA.value()).asText());;
            }
            return repositoryCoordinatorDefinition;
        }
    }


}
