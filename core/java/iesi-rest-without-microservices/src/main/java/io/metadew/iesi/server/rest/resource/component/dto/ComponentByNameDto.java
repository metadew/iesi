package io.metadew.iesi.server.rest.resource.component.dto;

import io.metadew.iesi.server.rest.resource.Dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class ComponentByNameDto extends Dto {

    private String type;
    private String name;
    private String description;
    private List<Long> versions;


}
