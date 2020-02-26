package io.metadew.iesi.server.rest.resource.script.dto;

import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.action.key.ActionParameterKey;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import lombok.*;
import org.springframework.hateoas.ResourceSupport;


@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class ScriptActionParameterDto extends ResourceSupport {

    private String name;
    private String value;

    public ActionParameter convertToEntity(String scriptName, long version, String actionId){
        return new ActionParameter(new ActionParameterKey(scriptName, version, IdentifierTools.getActionIdentifier(actionId), name), value);

    }

}
