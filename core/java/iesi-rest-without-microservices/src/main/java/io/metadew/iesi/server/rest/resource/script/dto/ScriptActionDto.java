package io.metadew.iesi.server.rest.resource.script.dto;

import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.action.key.ActionKey;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class ScriptActionDto extends ResourceSupport {

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
    private List<ScriptActionParameterDto>  parameters;

    public Action convertToEntity(String scriptName, long version){
        return new Action(new ActionKey(scriptName, version, IdentifierTools.getActionIdentifier(name)),
                number, type, this.name, description, component, condition, iteration, errorExpected ? "y" : "n",
                errorStop ? "y" : "n", Integer.toString(retries), parameters.stream()
                .map(parameter -> parameter.convertToEntity(scriptName, version, IdentifierTools.getActionIdentifier(name)))
                .collect(Collectors.toList()));

    }

}
