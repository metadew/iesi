package io.metadew.iesi.common.configuration.metadata.repository.coordinator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.mssql.MssqlMetadataRepositoryCoordinationDefinitionJsonComponent;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.mssql.MssqlMetadataRepositoryCoordinatorDefinition;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.h2.H2MetadataRepositoryCoordinationDefinitionJsonComponent;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.h2.H2MetadataRepositoryCoordinatorDefinition;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.mysql.MysqlMetadataRepositoryCoordinationDefinitionJsonComponent;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.mysql.MysqlMetadataRepositoryCoordinatorDefinition;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.mysql.MysqlMetadataRepositoryCoordinatorService;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.netezza.NetezzaMetadataRepositoryCoordinationDefinitionJsonComponent;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.netezza.NetezzaMetadataRepositoryCoordinatorDefinition;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.oracle.OracleMetadataRepositoryCoordinationDefinitionJsonComponent;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.oracle.OracleMetadataRepositoryCoordinatorDefinition;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.postgresql.PostgresqlMetadataRepositoryCoordinationDefinitionJsonComponent;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.postgresql.PostgresqlMetadataRepositoryCoordinatorDefinition;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.sqlite.SQLiteMetadataRepositoryCoordinatorDefinition;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.sqlite.SqliteMetadataRepositoryCoordinationDefinitionJsonComponent;

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
        WRITER_KEY("writer"),
        INIT_SQL("init_sql");

        private final String label;

        Field(String label) {
            this.label = label;
        }

        public String value() {
            return label;
        }
    }

    public static class Deserializer extends JsonDeserializer<MetadataRepositoryCoordinatorDefinition> {

        @Override
        public MetadataRepositoryCoordinatorDefinition deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            String type = node.get(Field.TYPE_KEY.value()).asText();
            if (type.equalsIgnoreCase(SqliteMetadataRepositoryCoordinationDefinitionJsonComponent.Field.TYPE.value())) {
                return jsonParser.getCodec().treeToValue(node, SQLiteMetadataRepositoryCoordinatorDefinition.class);
            } else if (type.equalsIgnoreCase(PostgresqlMetadataRepositoryCoordinationDefinitionJsonComponent.Field.TYPE.value())) {
                return jsonParser.getCodec().treeToValue(node, PostgresqlMetadataRepositoryCoordinatorDefinition.class);
            } else if (type.equalsIgnoreCase(OracleMetadataRepositoryCoordinationDefinitionJsonComponent.Field.TYPE.value())) {
                return jsonParser.getCodec().treeToValue(node, OracleMetadataRepositoryCoordinatorDefinition.class);
            } else if (type.equalsIgnoreCase(NetezzaMetadataRepositoryCoordinationDefinitionJsonComponent.Field.TYPE.value())) {
                return jsonParser.getCodec().treeToValue(node, NetezzaMetadataRepositoryCoordinatorDefinition.class);
            } else if (type.equalsIgnoreCase(MssqlMetadataRepositoryCoordinationDefinitionJsonComponent.Field.TYPE.value())) {
                return jsonParser.getCodec().treeToValue(node, MssqlMetadataRepositoryCoordinatorDefinition.class);
            } else if (type.equalsIgnoreCase(H2MetadataRepositoryCoordinationDefinitionJsonComponent.Field.TYPE.value())) {
                return jsonParser.getCodec().treeToValue(node, H2MetadataRepositoryCoordinatorDefinition.class);
            } else if (type.equalsIgnoreCase(MysqlMetadataRepositoryCoordinationDefinitionJsonComponent.Field.TYPE.value())) {
                return jsonParser.getCodec().treeToValue(node, MysqlMetadataRepositoryCoordinatorDefinition.class);
            } else {
                throw JsonMappingException.from(jsonParser, MessageFormat.format("Cannot deserialize RepositoryCoordinatorDefinition object of type {0}", type));
            }
        }
    }

    public static void setDefaultInformation(MetadataRepositoryCoordinatorDefinition metadataRepositoryCoordinatorDefinition, JsonNode jsonNode, JsonParser jsonParser) throws JsonProcessingException {
        // Mandatory
        metadataRepositoryCoordinatorDefinition.setType(jsonNode.get(Field.TYPE_KEY.value()).asText());
        // Optional
        if (jsonNode.hasNonNull(Field.CONNECTION_KEY.value())) {
            metadataRepositoryCoordinatorDefinition.setConnection(jsonNode.get(Field.CONNECTION_KEY.value()).asText());
        }
        if (jsonNode.hasNonNull(Field.OWNER_KEY.value())) {
            metadataRepositoryCoordinatorDefinition.setOwner(jsonParser.getCodec().treeToValue(jsonNode.get(Field.OWNER_KEY.value()), MetadataRepositoryCoordinatorProfileDefinition.class));
        }
        if (jsonNode.hasNonNull(Field.WRITER_KEY.value())) {
            metadataRepositoryCoordinatorDefinition.setWriter(jsonParser.getCodec().treeToValue(jsonNode.get(Field.WRITER_KEY.value()), MetadataRepositoryCoordinatorProfileDefinition.class));
        }
        if (jsonNode.hasNonNull(Field.READER_KEY.value())) {
            metadataRepositoryCoordinatorDefinition.setReader(jsonParser.getCodec().treeToValue(jsonNode.get(Field.READER_KEY.value()), MetadataRepositoryCoordinatorProfileDefinition.class));
        }

        if (jsonNode.hasNonNull(Field.INIT_SQL.value())) {
            metadataRepositoryCoordinatorDefinition.setInitSql(jsonNode.get(Field.INIT_SQL.value()).asText());
        }
    }


}
