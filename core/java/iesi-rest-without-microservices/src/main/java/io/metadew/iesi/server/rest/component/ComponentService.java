package io.metadew.iesi.server.rest.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.metadata.configuration.component.ComponentConfiguration;
import io.metadew.iesi.metadata.configuration.type.ActionTypeConfiguration;
import io.metadew.iesi.metadata.configuration.type.ComponentTypeConfiguration;
import io.metadew.iesi.metadata.definition.Metadata;
import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import io.metadew.iesi.server.rest.component.dto.ComponentDto;
import io.metadew.iesi.server.rest.component.dto.IComponentDtoService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@ConditionalOnWebApplication
@Log4j2
public class ComponentService implements IComponentService {

    private final ComponentConfiguration componentConfiguration;
    private final IComponentDtoService componentDtoService;
    private final ObjectMapper objectMapper;

    private final ComponentTypeConfiguration componentTypeConfiguration;

    @Autowired
    public ComponentService(
            ComponentConfiguration componentConfiguration,
            IComponentDtoService componentDtoService,
            ObjectMapper objectMapper,
            ComponentTypeConfiguration componentTypeConfiguration
    ) {
        this.componentConfiguration = componentConfiguration;
        this.componentDtoService = componentDtoService;
        this.objectMapper = objectMapper;
        this.componentTypeConfiguration = componentTypeConfiguration;
    }

    public List<Component> getAll() {
        return componentConfiguration.getAll();
    }

    public List<Component> getByName(String name) {
        return componentConfiguration.getByID(IdentifierTools.getComponentIdentifier(name));
    }

    @Override
    public List<Component> importComponents(String textPLain) {
        DataObjectOperation dataObjectOperation = new DataObjectOperation(textPLain);

        System.out.println("DATA OBJECT OPERATION: " + dataObjectOperation);

        return dataObjectOperation.getDataObjects().stream().map((dataObject -> {
            Component component = (Component) objectMapper.convertValue(dataObject, Metadata.class);

            System.out.println("CHEESH: " + componentTypeConfiguration.getComponentType(component.getType()));

            if (!componentTypeConfiguration.getComponentType(component.getType()).isPresent()) {
                throw new RuntimeException("Component type " + component.getType() + " not found");
            }


            if (componentConfiguration.getByNameAndVersion(component.getName(), component.getMetadataKey().getVersionNumber()).isPresent()) {
                log.info(String.format("Component %s with version %s already exists in design repository. Updating to new definition", component.getName(), component.getVersion().getMetadataKey().getComponentKey().getVersionNumber()));
                componentConfiguration.update(component);
            } else {
                componentConfiguration.insert(component);
            }

            return component;
        })).collect(Collectors.toList());
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
