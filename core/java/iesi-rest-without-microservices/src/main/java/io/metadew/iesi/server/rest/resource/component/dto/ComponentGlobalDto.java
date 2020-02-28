package io.metadew.iesi.server.rest.resource.component.dto;

import io.metadew.iesi.server.rest.resource.Dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class ComponentGlobalDto extends Dto {

    private String type;
    private String name;
    private String description;

}



