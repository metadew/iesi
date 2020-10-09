package io.metadew.iesi.server.rest.script.dto;

import io.metadew.iesi.server.rest.script.dto.action.ActionDto;
import io.metadew.iesi.server.rest.script.dto.label.ScriptLabelDto;
import io.metadew.iesi.server.rest.script.dto.parameter.ScriptParameterDto;
import io.metadew.iesi.server.rest.script.dto.version.ScriptVersionDto;
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
public class ScriptPostDto extends RepresentationModel<ScriptPostDto> {

    private String name;
    private String securityGroupName;
    private String description;
    private ScriptVersionDto version;
    private List<ScriptParameterDto> parameters;
    private List<ActionDto> actions;
    private List<ScriptLabelDto> labels;


}
