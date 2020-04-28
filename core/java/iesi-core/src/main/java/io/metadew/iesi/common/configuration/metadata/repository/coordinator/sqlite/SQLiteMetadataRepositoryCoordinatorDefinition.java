package io.metadew.iesi.common.configuration.metadata.repository.coordinator.sqlite;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.MetadataRepositoryCoordinatorDefinition;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.MetadataRepositoryCoordinatorProfileDefinition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonDeserialize(using = SqliteMetadataRepositoryCoordinationDefinitionJsonComponent.Deserializer.class)
public class SQLiteMetadataRepositoryCoordinatorDefinition extends MetadataRepositoryCoordinatorDefinition {

    private String file;

    public SQLiteMetadataRepositoryCoordinatorDefinition() {
        super();
    }

    public SQLiteMetadataRepositoryCoordinatorDefinition(String type, MetadataRepositoryCoordinatorProfileDefinition owner, MetadataRepositoryCoordinatorProfileDefinition writer, MetadataRepositoryCoordinatorProfileDefinition user, String file) {
        super(type, owner, writer, user);
        this.file = file;
    }
}
