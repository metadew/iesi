package io.metadew.iesi.framework.configuration.metadata.repository.coordinator;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonDeserialize(using = OracleMetadataRepositoryCoordinationDefinitionJsonComponent.Deserializer.class)
public class OracleRepositoryCoordinatorDefinition extends RepositoryCoordinatorDefinition {

    private String host;
    private String port;
    private String name;
    private String service;
    private String tnsAlias;
    private String schema;

    public OracleRepositoryCoordinatorDefinition() {
        super();
    }

    public OracleRepositoryCoordinatorDefinition(String type, RepositoryCoordinatorProfileDefinition owner, RepositoryCoordinatorProfileDefinition writer, RepositoryCoordinatorProfileDefinition user, String host, String port, String name, String service, String tnsAlias, String schema) {
        super(type, owner, writer, user);
        this.host = host;
        this.port = port;
        this.name = name;
        this.service = service;
        this.tnsAlias = tnsAlias;
        this.schema = schema;
    }
}
