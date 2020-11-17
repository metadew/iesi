package io.metadew.iesi.server.rest.script.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.metadew.iesi.server.rest.script.dto.action.ActionDto;
import io.metadew.iesi.server.rest.script.dto.expansions.ScriptExecutionInformation;
import io.metadew.iesi.server.rest.script.dto.expansions.ScriptSchedulingInformation;
import io.metadew.iesi.server.rest.script.dto.label.ScriptLabelDto;
import io.metadew.iesi.server.rest.script.dto.parameter.ScriptParameterDto;
import io.metadew.iesi.server.rest.script.dto.version.ScriptVersionDto;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;


import java.util.HashSet;
import java.util.Set;

@Builder
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Relation(value = "script", collectionRelation = "scripts")
public class ScriptDto extends RepresentationModel<ScriptDto>  {

    private String name;
    private String description;
    private ScriptVersionDto version;
    private Set<ScriptParameterDto> parameters = new HashSet<>();
    private Set<ActionDto> actions = new HashSet<>();
    private Set<ScriptLabelDto> labels = new HashSet<>();
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("execution")
    private ScriptExecutionInformation scriptExecutionInformation;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("scheduling")
    private ScriptSchedulingInformation scriptSchedulingInformation;

    public void addActionDto(ActionDto actionDto) {
        actions.add(actionDto);
    }

    public void addScriptLabelDto(ScriptLabelDto scriptLabelDto) {
        labels.add(scriptLabelDto);
    }

}
