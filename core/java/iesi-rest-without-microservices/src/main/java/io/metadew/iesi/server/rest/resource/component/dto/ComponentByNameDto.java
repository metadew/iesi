package io.metadew.iesi.server.rest.resource.component.dto;

import io.metadew.iesi.server.rest.resource.Dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ComponentByNameDto extends Dto {

    private String name;
    private String type;
    private String description;
    private List<Long> versions;

}
