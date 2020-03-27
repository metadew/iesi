package io.metadew.iesi.common.configuration.metadata.repository.coordinator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.text.MessageFormat;

public class MetadataRepositoryCoordinationDefinitionJsonComponent {

    public static boolean hasConnectionUrlSet(JsonNode node) {
        return node.hasNonNull(Field.CONNECTION_KEY.value());
    }

    public enum Field {
        TYPE_KEY("type"),
        CONNECTION_KEY("connection"),
        OWNER_KEY("owner"),
        READER_KEY("reader"),
        WRITER_KEY("writer");

        private final String label;

        Field(String label) {
            this.label = label;
        }

        public String value() {
            return label;
        }
    }

    public static class Deserializer extends JsonDeserializer<RepositoryCoordinatorDefinition> {

        @Override
        public RepositoryCoordinatorDefinition deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            String type = node.get(Field.TYPE_KEY.value()).asText();
            if (type.equalsIgnoreCase(SqliteMetadataRepositoryCoordinationDefinitionJsonComponent.Field.TYPE.value())) {
                return jsonParser.getCodec().treeToValue(node, SQLiteRepositoryCoordinatorDefinition.class);
            } else if (type.equalsIgnoreCase(PostgresqlMetadataRepositoryCoordinationDefinitionJsonComponent.Field.TYPE.value())) {
                return jsonParser.getCodec().treeToValue(node, PostgresqlRepositoryCoordinatorDefinition.class);
            } else if (type.equalsIgnoreCase(OracleMetadataRepositoryCoordinationDefinitionJsonComponent.Field.TYPE.value())) {
                return jsonParser.getCodec().treeToValue(node, OracleRepositoryCoordinatorDefinition.class);
            } else if (type.equalsIgnoreCase(NetezzaMetadataRepositoryCoordinationDefinitionJsonComponent.Field.TYPE.value())) {
                return jsonParser.getCodec().treeToValue(node, NetezzaRepositoryCoordinatorDefinition.class);
            } else if (type.equalsIgnoreCase(MssqlMetadataRepositoryCoordinationDefinitionJsonComponent.Field.TYPE.value())) {
                return jsonParser.getCodec().treeToValue(node, MssqlRepositoryCoordinatorDefinition.class);
            } else if (type.equalsIgnoreCase(H2MetadataRepositoryCoordinationDefinitionJsonComponent.Field.TYPE.value())) {
                return jsonParser.getCodec().treeToValue(node, H2RepositoryCoordinatorDefinition.class);
            } else {
                throw JsonMappingException.from(jsonParser, MessageFormat.format("Cannot deserialize RepositoryCoordinatorDefinition object of type {0}", type));
            }
        }
    }

    public static void setDefaultInformation(RepositoryCoordinatorDefinition repositoryCoordinatorDefinition, JsonNode jsonNode, JsonParser jsonParser) throws JsonProcessingException {
        // Mandatory
        repositoryCoordinatorDefinition.setType(jsonNode.get(Field.TYPE_KEY.value()).asText());
        // Optional
        if (jsonNode.hasNonNull(Field.CONNECTION_KEY.value())) {
            repositoryCoordinatorDefinition.setConnection(jsonNode.get(Field.CONNECTION_KEY.value()).asText());
        }
        if (jsonNode.hasNonNull(Field.OWNER_KEY.value())) {
            repositoryCoordinatorDefinition.setOwner(jsonParser.getCodec().treeToValue(jsonNode.get(Field.OWNER_KEY.value()), RepositoryCoordinatorProfileDefinition.class));
        }
        if (jsonNode.hasNonNull(Field.WRITER_KEY.value())) {
            repositoryCoordinatorDefinition.setWriter(jsonParser.getCodec().treeToValue(jsonNode.get(Field.WRITER_KEY.value()), RepositoryCoordinatorProfileDefinition.class));
        }
        if (jsonNode.hasNonNull(Field.READER_KEY.value())) {
            repositoryCoordinatorDefinition.setReader(jsonParser.getCodec().treeToValue(jsonNode.get(Field.READER_KEY.value()), RepositoryCoordinatorProfileDefinition.class));
        }
    }


}
