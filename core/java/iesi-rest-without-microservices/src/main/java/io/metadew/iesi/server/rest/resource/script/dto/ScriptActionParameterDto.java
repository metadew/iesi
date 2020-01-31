package io.metadew.iesi.server.rest.resource.script.dto;

import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.action.key.ActionParameterKey;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.ResourceSupport;

@Getter
@Setter
public class ScriptActionParameterDto extends ResourceSupport {

    private String name;
    private String value;

    public ScriptActionParameterDto() {}

    public ScriptActionParameterDto(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public ActionParameter convertToEntity(String scriptName, long version, String actionId){
        return new ActionParameter(new ActionParameterKey(scriptName, version, IdentifierTools.getActionIdentifier(actionId), name), value);

    }

}
