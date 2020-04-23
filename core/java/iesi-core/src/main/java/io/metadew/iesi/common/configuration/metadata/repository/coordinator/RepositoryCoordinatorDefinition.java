package io.metadew.iesi.common.configuration.metadata.repository.coordinator;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(using = MetadataRepositoryCoordinationDefinitionJsonComponent.Deserializer.class)
public abstract class RepositoryCoordinatorDefinition {

    private String type;
    private String connection;
    private RepositoryCoordinatorProfileDefinition owner;
    private RepositoryCoordinatorProfileDefinition writer;
    private RepositoryCoordinatorProfileDefinition reader;

    public RepositoryCoordinatorDefinition(String type, RepositoryCoordinatorProfileDefinition owner, RepositoryCoordinatorProfileDefinition writer, RepositoryCoordinatorProfileDefinition reader) {
        this.type = type;
        this.owner = owner;
        this.writer = writer;
        this.reader = reader;
    }

    public Optional<String> getConnection() {return Optional.ofNullable(connection);}

}
