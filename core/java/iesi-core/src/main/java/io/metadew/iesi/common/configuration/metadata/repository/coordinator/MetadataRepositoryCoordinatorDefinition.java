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
public abstract class MetadataRepositoryCoordinatorDefinition {

    private String type;
    private String connection;
    private String initSql;
    private MetadataRepositoryCoordinatorProfileDefinition owner;
    private MetadataRepositoryCoordinatorProfileDefinition writer;
    private MetadataRepositoryCoordinatorProfileDefinition reader;

    public MetadataRepositoryCoordinatorDefinition(String type, MetadataRepositoryCoordinatorProfileDefinition owner, MetadataRepositoryCoordinatorProfileDefinition writer, MetadataRepositoryCoordinatorProfileDefinition reader) {
        this.type = type;
        this.owner = owner;
        this.writer = writer;
        this.reader = reader;
    }

    public Optional<String> getConnection() {return Optional.ofNullable(connection);}

}
