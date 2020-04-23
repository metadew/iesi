package io.metadew.iesi.server.rest.resource.script.dto;

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
public class ScriptByNameDto extends RepresentationModel<ScriptByNameDto> {

    private String name;
    private String description;
    private List<Long> versions;

}
