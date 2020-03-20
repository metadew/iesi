package io.metadew.iesi.framework.configuration.metadata.repository.coordinator;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Optional;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonDeserialize(using = H2MetadataRepositoryCoordinationDefinitionJsonComponent.Deserializer.class)
public class H2RepositoryCoordinatorDefinition extends RepositoryCoordinatorDefinition {

    private String host;
    private int port;
    private String file;
    private String schema;
    private String mode;
    private String databaseName;

    public H2RepositoryCoordinatorDefinition() {
        super();
    }

    public H2RepositoryCoordinatorDefinition(String type, RepositoryCoordinatorProfileDefinition owner, RepositoryCoordinatorProfileDefinition writer, RepositoryCoordinatorProfileDefinition user, String host, int port, String file, String schema, String mode, String databaseName) {
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
