package io.metadew.iesi.server.rest.component;

import io.metadew.iesi.metadata.configuration.component.ComponentConfiguration;
import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import io.metadew.iesi.server.rest.component.dto.ComponentDto;
import io.metadew.iesi.server.rest.component.dto.IComponentDtoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@ConditionalOnWebApplication
public class ComponentService implements IComponentService {

    private ComponentConfiguration componentConfiguration;
    private IComponentDtoService componentDtoService;

    @Autowired
    public ComponentService(ComponentConfiguration componentConfiguration, IComponentDtoService componentDtoService) {
        this.componentConfiguration = componentConfiguration;
        this.componentDtoService = componentDtoService;
    }

    public List<Component> getAll() {
        return componentConfiguration.getAll();
    }

    public List<Component> getByName(String name) {
        return componentConfiguration.getByID(IdentifierTools.getComponentIdentifier(name));
    }

    public Optional<Component> getByNameAndVersion(String name, long version) {
        return componentConfiguration.get(new ComponentKey(IdentifierTools.getComponentIdentifier(name), version));
    }

    public void createComponent(ComponentDto componentDto) {
        componentConfiguration.insert(componentDtoService.convertToEntity(componentDto));
    }

    public void updateComponent(ComponentDto componentDto) {
        componentConfiguration.update(componentDtoService.convertToEntity(componentDto));
    }

    public void updateComponents(List<ComponentDto> componentDto) {
        componentDto.forEach(this::updateComponent);
    }

    @Override
    public void deleteAll() {
        componentConfiguration.deleteAll();
    }

    @Override
    public void deleteByName(String name) {
        componentConfiguration.deleteById(IdentifierTools.getComponentIdentifier(name));
    }

    @Override
    public void deleteByNameAndVersion(String name, long version) {
        componentConfiguration.delete(new ComponentKey(IdentifierTools.getComponentIdentifier(name), version));
    }

}
