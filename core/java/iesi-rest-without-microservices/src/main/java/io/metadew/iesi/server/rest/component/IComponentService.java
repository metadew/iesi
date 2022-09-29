package io.metadew.iesi.server.rest.component;

import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.server.rest.component.dto.ComponentDto;

import java.util.List;
import java.util.Optional;

public interface IComponentService {

    List<Component> getAll();

    List<Component> getByName(String name);

    List<Component> importComponents(String textPLain);

    Optional<Component> getByNameAndVersion(String name, long version);

    void createComponent(ComponentDto componentDto);

    void updateComponent(ComponentDto componentDto);

    void updateComponents(List<ComponentDto> componentDto);

    void deleteAll();

    void deleteByName(String name);

    void deleteByNameAndVersion(String name, long version);

}
