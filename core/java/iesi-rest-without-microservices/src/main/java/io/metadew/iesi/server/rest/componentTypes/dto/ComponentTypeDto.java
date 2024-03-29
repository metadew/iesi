package io.metadew.iesi.server.rest.componentTypes.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ComponentTypeDto extends RepresentationModel<ComponentTypeDto> {
    private String name;
    private String description;
    @JsonProperty("parameters")
    private List<ComponentTypeParameterDto> componentTypeParameterDtos;
}
