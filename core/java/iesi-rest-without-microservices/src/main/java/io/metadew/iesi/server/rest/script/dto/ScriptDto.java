package io.metadew.iesi.server.rest.script.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.metadew.iesi.server.rest.script.dto.action.ActionDto;
import io.metadew.iesi.server.rest.script.dto.expansions.ScriptExecutionInformation;
import io.metadew.iesi.server.rest.script.dto.expansions.ScriptSchedulingInformation;
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
public class ScriptDto extends RepresentationModel<ScriptDto> {

    private String name;
    private String description;
    private ScriptVersionDto version;
    private List<ScriptParameterDto> parameters;
    private List<ActionDto> actions;
    private List<ScriptLabelDto> labels;
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
