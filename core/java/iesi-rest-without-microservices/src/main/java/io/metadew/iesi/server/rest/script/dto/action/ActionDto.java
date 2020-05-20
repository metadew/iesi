package io.metadew.iesi.server.rest.script.dto.action;

import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.action.key.ActionKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ActionDto extends RepresentationModel<ActionDto> {

    private long number;
    private String name;
    private String type;
    private String description;
    private String component;
    private String condition;
    private String iteration;
    private boolean errorExpected;
    private boolean errorStop;
    private int retries;
    private List<ActionParameterDto> parameters;

    public Action convertToEntity(String scriptId, long version) {
        return new Action(new ActionKey(new ScriptKey(scriptId, version), IdentifierTools.getActionIdentifier(name)),
                number, type, this.name, description, component, condition, iteration, errorExpected ? "y" : "n",
                errorStop ? "y" : "n", Integer.toString(retries), parameters.stream()
                .map(parameter -> parameter.convertToEntity(scriptId, version, IdentifierTools.getActionIdentifier(name)))
                .collect(Collectors.toList()));

    }

    public void addActionParameterDto(ActionParameterDto actionParameterDto) {
        parameters.add(actionParameterDto);
    }

}
