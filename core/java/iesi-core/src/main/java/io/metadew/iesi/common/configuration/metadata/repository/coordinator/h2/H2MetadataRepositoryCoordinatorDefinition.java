package io.metadew.iesi.common.configuration.metadata.repository.coordinator.h2;

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
@JsonDeserialize(using = H2MetadataRepositoryCoordinationDefinitionJsonComponent.Deserializer.class)
public class H2MetadataRepositoryCoordinatorDefinition extends MetadataRepositoryCoordinatorDefinition {

    private String host;
    private int port;
    private String file;
    private String schema;
    private String mode;
    private String databaseName;

    public H2MetadataRepositoryCoordinatorDefinition() {
        super();
    }

    public H2MetadataRepositoryCoordinatorDefinition(String type, MetadataRepositoryCoordinatorProfileDefinition owner, MetadataRepositoryCoordinatorProfileDefinition writer, MetadataRepositoryCoordinatorProfileDefinition user, String host, int port, String file, String schema, String mode, String databaseName) {
        super(type, owner, writer, user);
        this.host = host;
        this.port = port;
        this.file = file;
        this.schema = schema;
        this.mode = mode;
        this.databaseName = databaseName;
    }

    public Optional<String> getSchema() {
        return Optional.ofNullable(schema);
    }

}
