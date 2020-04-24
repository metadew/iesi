package io.metadew.iesi.common.configuration.metadata.repository.coordinator.postgresql;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.MetadataRepositoryCoordinatorDefinition;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.MetadataRepositoryCoordinatorProfileDefinition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Optional;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonDeserialize(using = PostgresqlMetadataRepositoryCoordinationDefinitionJsonComponent.Deserializer.class)
public class PostgresqlMetadataRepositoryCoordinatorDefinition extends MetadataRepositoryCoordinatorDefinition {

    private String host;
    private int port;
    private String database;
    private String schema;

    public PostgresqlMetadataRepositoryCoordinatorDefinition() {
        super();
    }

    public PostgresqlMetadataRepositoryCoordinatorDefinition(String type, MetadataRepositoryCoordinatorProfileDefinition owner, MetadataRepositoryCoordinatorProfileDefinition writer, MetadataRepositoryCoordinatorProfileDefinition user, String host, int port, String database, String schema) {
        super(type, owner, writer, user);
        this.host = host;
        this.port = port;
        this.database = database;
        this.schema = schema;
    }

    public Optional<String> getSchema() {return Optional.ofNullable(schema);}
}
