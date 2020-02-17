package io.metadew.iesi.framework.configuration.metadata.repository.coordinator;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(using = MetadataRepositoryCoordinationDefinitionJsonComponent.Deserializer.class)
public abstract class RepositoryCoordinatorDefinition {

    private String type;
    private RepositoryCoordinatorProfileDefinition owner;
    private RepositoryCoordinatorProfileDefinition writer;
    private RepositoryCoordinatorProfileDefinition reader;

}
