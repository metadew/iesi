package io.metadew.iesi.server.rest.resource.script.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ScriptGlobalDto extends RepresentationModel<ScriptGlobalDto> {

    private String name;
    private String description;

}

