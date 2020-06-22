package io.metadew.iesi.server.rest.environment;

import io.metadew.iesi.metadata.definition.environment.Environment;
import io.metadew.iesi.server.rest.environment.dto.EnvironmentDto;

import java.util.List;
import java.util.Optional;

public interface IEnvironmentService {

    public List<Environment> getAll();

    public Optional<Environment> getByName(String name);

    public void createEnvironment(EnvironmentDto environmentDto);

    public void updateEnvironment(EnvironmentDto environmentDto);

    public void updateEnvironments(List<EnvironmentDto> environmentDtos);

    public void deleteAll();

    public void deleteByName(String name);

}
