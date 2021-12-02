package io.metadew.iesi.server.rest.component.dto;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.Set;


@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Relation(value = "component", collectionRelation = "components")
public class ComponentDto extends RepresentationModel<ComponentDto> {

    private String type;
    private String securityGroupName;
    private String name;
    private String description;
    private ComponentVersionDto version;

    private Set<ComponentParameterDto> parameters;
    private Set<ComponentAttributeDto> attributes;

}