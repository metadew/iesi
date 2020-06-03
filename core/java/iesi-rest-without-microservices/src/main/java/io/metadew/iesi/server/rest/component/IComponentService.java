package io.metadew.iesi.server.rest.component;

import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.server.rest.component.dto.ComponentDto;

import java.util.List;
import java.util.Optional;

public interface IComponentService {

    public List<Component> getAll();

    public List<Component> getByName(String name);

    public Optional<Component> getByNameAndVersion(String name, long version);

    public void createComponent(ComponentDto componentDto);

    public void updateComponent(ComponentDto componentDto);

    public void updateComponents(List<ComponentDto> componentDto);

    public void deleteAll();

    public void deleteByName(String name);

    public void deleteByNameAndVersion(String name, long version);

}
