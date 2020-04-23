package io.metadew.iesi.common.configuration.metadata.repository.coordinator;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Optional;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class NetezzaRepositoryCoordinatorDefinition extends RepositoryCoordinatorDefinition {

    private String host;
    private int port;
    private String name;
    private String schema;
    private String database;

    public NetezzaRepositoryCoordinatorDefinition() {
        super();
    }

    public NetezzaRepositoryCoordinatorDefinition(String type, RepositoryCoordinatorProfileDefinition owner, RepositoryCoordinatorProfileDefinition writer, RepositoryCoordinatorProfileDefinition user, String host, int port, String name, String schema, String database) {
        super(type, owner, writer, user);
        this.host = host;
        this.port = port;
        this.name = name;
        this.schema = schema;
        this.database = database;
    }


    public Optional<String> getSchema() {
        return Optional.ofNullable(schema);
    }

}
