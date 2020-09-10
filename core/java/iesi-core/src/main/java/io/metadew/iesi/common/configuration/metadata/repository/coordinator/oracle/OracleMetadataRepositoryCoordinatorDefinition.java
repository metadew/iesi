package io.metadew.iesi.common.configuration.metadata.repository.coordinator.oracle;

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
@JsonDeserialize(using = OracleMetadataRepositoryCoordinationDefinitionJsonComponent.Deserializer.class)
public class OracleMetadataRepositoryCoordinatorDefinition extends MetadataRepositoryCoordinatorDefinition {

    private String mode;
    private String host;
    private int port;
    private String service;
    private String tnsAlias;
    private String schema;

    public OracleMetadataRepositoryCoordinatorDefinition() {
        super();
    }

    public OracleMetadataRepositoryCoordinatorDefinition(String type, String mode, MetadataRepositoryCoordinatorProfileDefinition owner, MetadataRepositoryCoordinatorProfileDefinition writer, MetadataRepositoryCoordinatorProfileDefinition user, String host, int port, String service, String tnsAlias, String schema) {
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
