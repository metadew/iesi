package io.metadew.iesi.server.rest.resource.script.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class ScriptByNameDto extends ResourceSupport {

    private String name;
    private String description;
    private List<Long> versions;

}
