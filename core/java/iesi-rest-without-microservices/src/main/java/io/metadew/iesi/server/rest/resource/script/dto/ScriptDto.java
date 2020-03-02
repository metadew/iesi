package io.metadew.iesi.server.rest.resource.script.dto;

import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ScriptDto extends ResourceSupport {

    private String name;
    private String description;
    private ScriptVersionDto version;
    private List<ScriptParameterDto> parameters;
    private List<ActionDto> actions;
    private List<ScriptLabelDto> labels;

    public Script convertToEntity() {
        return new Script(new ScriptKey(IdentifierTools.getScriptIdentifier(name), version.convertToEntity(IdentifierTools.getScriptIdentifier(name)).getNumber()),
                name, description,  version.convertToEntity(IdentifierTools.getScriptIdentifier(name)),
                parameters.stream()
                        .map(parameter -> parameter.convertToEntity(new ScriptKey(IdentifierTools.getScriptIdentifier(name), version.getNumber())))
                        .collect(Collectors.toList()),
                actions.stream()
                        .map(action -> action.convertToEntity(IdentifierTools.getScriptIdentifier(name), version.getNumber()))
                        .collect(Collectors.toList()),
                labels.stream()
                        .map(label -> label.convertToEntity(new ScriptKey(IdentifierTools.getScriptIdentifier(name), version.getNumber())))
                        .collect(Collectors.toList()));
    }

}
