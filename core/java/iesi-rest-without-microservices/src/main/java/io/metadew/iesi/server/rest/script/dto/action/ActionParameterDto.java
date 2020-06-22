package io.metadew.iesi.server.rest.script.dto.action;

import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.action.key.ActionParameterKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;


@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ActionParameterDto extends RepresentationModel<ActionParameterDto> {

    private String name;
    private String value;

    public ActionParameter convertToEntity(String scriptName, long version, String actionId){
        return new ActionParameter(new ActionParameterKey(scriptName, version, actionId, name), value);

    }

}
