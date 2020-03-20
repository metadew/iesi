package io.metadew.iesi.framework.configuration.metadata.repository.coordinator;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Optional;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonDeserialize(using = OracleMetadataRepositoryCoordinationDefinitionJsonComponent.Deserializer.class)
public class OracleRepositoryCoordinatorDefinition extends RepositoryCoordinatorDefinition {

    private String mode;
    private String host;
    private int port;
    private String service;
    private String tnsAlias;
    private String schema;

    public OracleRepositoryCoordinatorDefinition() {
        super();
    }

    public OracleRepositoryCoordinatorDefinition(String type, String mode, RepositoryCoordinatorProfileDefinition owner, RepositoryCoordinatorProfileDefinition writer, RepositoryCoordinatorProfileDefinition user, String host, int port, String service, String tnsAlias, String schema) {
        super(type, owner, writer, user);
        this.mode = mode;
        this.host = host;
        this.port = port;
        this.service = service;
        this.tnsAlias = tnsAlias;
        this.schema = schema;
    }

    public Optional<String> getSchema() {
        return Optional.ofNullable(schema);
    }
}
