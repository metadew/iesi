package io.metadew.iesi.framework.configuration.metadata.repository.coordinator;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonDeserialize(using = SqliteMetadataRepositoryCoordinationDefinitionJsonComponent.Deserializer.class)
public class SQLiteRepositoryCoordinatorDefinition extends RepositoryCoordinatorDefinition {

    private String file;

    public SQLiteRepositoryCoordinatorDefinition() {
        super();
    }

    public SQLiteRepositoryCoordinatorDefinition(String type, RepositoryCoordinatorProfileDefinition owner, RepositoryCoordinatorProfileDefinition writer, RepositoryCoordinatorProfileDefinition user, String file) {
        super(type, owner, writer, user);
        this.file = file;
    }
}
