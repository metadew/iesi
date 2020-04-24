package io.metadew.iesi.server.rest.resource.component.dto;

import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;
import java.util.stream.Collectors;


@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ComponentDto extends RepresentationModel<ComponentDto> {

    private String type;
    private String name;
    private String description;
    private ComponentVersionDto version;

    private List<ComponentParameterDto> parameters;
    private List<ComponentAttributeDto> attributes;

    public Component convertToEntity() {
        return new Component(new ComponentKey(IdentifierTools.getComponentIdentifier(name),
                version.getNumber()),
                type,
                name,
                description,
                version.convertToEntity(IdentifierTools.getComponentIdentifier(name)),
                parameters.stream()
                        .map(parameter -> parameter.convertToEntity(IdentifierTools.getComponentIdentifier(name), version.getNumber()))
                        .collect(Collectors.toList()),
                attributes.stream()
                        .map(attribute -> attribute.convertToEntity(IdentifierTools.getComponentIdentifier(name), version.getNumber()))
                        .collect(Collectors.toList()));
    }

}