package io.metadew.iesi.server.rest.script.dto;

import io.metadew.iesi.server.rest.script.dto.action.ActionDto;
import io.metadew.iesi.server.rest.script.dto.label.ScriptLabelDto;
import io.metadew.iesi.server.rest.script.dto.parameter.ScriptParameterDto;
import io.metadew.iesi.server.rest.script.dto.version.ScriptVersionDto;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScriptPostDto extends RepresentationModel<ScriptPostDto> {

    private String name;
    private String securityGroupName;
    private String description;
    private ScriptVersionDto version;
    private Set<ScriptParameterDto> parameters;
    private Set<ActionDto> actions;
    private Set<ScriptLabelDto> labels;
    private String deleted_At = "NA";


}
