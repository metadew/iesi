package io.metadew.iesi.framework.configuration.metadata.repository.coordinator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RepositoryCoordinatorProfileDefinition {

    private String user;
    private String password;

}
