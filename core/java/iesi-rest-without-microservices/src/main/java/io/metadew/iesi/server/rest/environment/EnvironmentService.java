package io.metadew.iesi.server.rest.environment;

import io.metadew.iesi.metadata.configuration.environment.EnvironmentConfiguration;
import io.metadew.iesi.metadata.definition.environment.Environment;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
import io.metadew.iesi.server.rest.environment.dto.EnvironmentDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EnvironmentService implements IEnvironmentService {

    private EnvironmentConfiguration environmentConfiguration;

    public EnvironmentService(EnvironmentConfiguration environmentConfiguration) {
        this.environmentConfiguration = environmentConfiguration;
    }

    public List<Environment> getAll() {
        return environmentConfiguration.getAll();
    }

    public Optional<Environment> getByName(String name) {
        return environmentConfiguration.get(new EnvironmentKey(name));
    }

    public void createEnvironment(EnvironmentDto environmentDto) {
        environmentConfiguration.insert(environmentDto.convertToEntity());
    }

    public void updateEnvironment(EnvironmentDto environmentDto) {
        environmentConfiguration.update(environmentDto.convertToEntity());
    }

    public void updateEnvironments(List<EnvironmentDto> environmentDtos) {
        environmentDtos.forEach(this::updateEnvironment);
    }

    public void deleteAll() {
        environmentConfiguration.deleteAll();
    }

    public void deleteByName(String name) {
        environmentConfiguration.delete(new EnvironmentKey(name));
    }

}
