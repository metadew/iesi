package io.metadew.iesi.common.configuration.metadata.repository.coordinator.mysql;

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
@JsonDeserialize(using = MysqlMetadataRepositoryCoordinationDefinitionJsonComponent.Deserializer.class)
public class MysqlMetadataRepositoryCoordinatorDefinition extends MetadataRepositoryCoordinatorDefinition {

    private String host;
    private int port;
    private String schema;
    private String initSql;

    public MysqlMetadataRepositoryCoordinatorDefinition() {
        super();
    }

    public MysqlMetadataRepositoryCoordinatorDefinition(String type, MetadataRepositoryCoordinatorProfileDefinition owner, MetadataRepositoryCoordinatorProfileDefinition writer, MetadataRepositoryCoordinatorProfileDefinition user, String host, int port, String schema, String initSql) {
        super(type, owner, writer, user);
        this.host = host;
        this.port = port;
        this.schema = schema;
    }

    public Optional<String> getSchema() {return Optional.ofNullable(schema);}
}
