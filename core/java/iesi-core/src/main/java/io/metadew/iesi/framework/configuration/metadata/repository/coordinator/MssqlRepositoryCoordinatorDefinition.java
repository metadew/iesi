package io.metadew.iesi.framework.configuration.metadata.repository.coordinator;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Optional;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonDeserialize(using = NetezzaMetadataRepositoryCoordinationDefinitionJsonComponent.Deserializer.class)
public class MssqlRepositoryCoordinatorDefinition extends RepositoryCoordinatorDefinition {

    private String host;
    private int port;
    private String database;
    private String schema;

    public MssqlRepositoryCoordinatorDefinition() {
        super();
    }

    public MssqlRepositoryCoordinatorDefinition(String type, RepositoryCoordinatorProfileDefinition owner, RepositoryCoordinatorProfileDefinition writer, RepositoryCoordinatorProfileDefinition user, String host, int port, String database, String schema) {
        super(type, owner, writer, user);
        this.host = host;
        this.port = port;
        this.database = database;
        this.schema = schema;
    }

    public Optional<String> getSchema() {
        return Optional.ofNullable(schema);
    }

}
