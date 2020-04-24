package io.metadew.iesi.common.configuration.metadata.repository;

import io.metadew.iesi.common.configuration.metadata.repository.coordinator.MetadataRepositoryCoordinatorDefinition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetadataRepositoryDefinition {

    private String instance;
    private List<String> categories;
    private MetadataRepositoryCoordinatorDefinition coordinator;

}
