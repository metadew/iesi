package io.metadew.iesi.server.rest.componentTypes;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.metadew.iesi.server.rest.componentTypes.parameter.ComponentTypeParameterDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ComponentTypeDto {
    private String name;
    private String description;
    @JsonProperty("parameters")
    private List<ComponentTypeParameterDto> componentTypeParameterDtos;
}
