package io.metadew.iesi.framework.configuration.metadata.repository;

import io.metadew.iesi.framework.configuration.metadata.repository.coordinator.RepositoryCoordinatorDefinition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetadataRepositoryDefinition {

    private String name;
    private String instance;
    private List<String> categories;
    private RepositoryCoordinatorDefinition coordinator;

}
