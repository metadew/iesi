package io.metadew.iesi.framework.configuration.metadata.repository.coordinator;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonDeserialize(using = PostgresqlMetadataRepositoryCoordinationDefinitionJsonComponent.Deserializer.class)
public class PostgresqlRepositoryCoordinatorDefinition extends RepositoryCoordinatorDefinition {

    private String host;
    private String port;
    private String name;
    private String schema;

    public PostgresqlRepositoryCoordinatorDefinition() {
        super();
    }

    public PostgresqlRepositoryCoordinatorDefinition(String type, RepositoryCoordinatorProfileDefinition owner, RepositoryCoordinatorProfileDefinition writer, RepositoryCoordinatorProfileDefinition user, String host, String port, String name, String schema) {
        super(type, owner, writer, user);
        this.host = host;
        this.port = port;
        this.name = name;
        this.schema = schema;
    }
}
