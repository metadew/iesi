package io.metadew.iesi.server.rest.script.dto.action;


import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.action.key.ActionKey;
import io.metadew.iesi.metadata.definition.action.key.ActionParameterKey;
import io.metadew.iesi.server.rest.script.dto.NoEmptyLinksRepresentationModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;



@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ActionParameterDto extends NoEmptyLinksRepresentationModel<ActionParameterDto> {

    private String name;
    private String value;

    public ActionParameter convertToEntity(ActionKey actionKey){
        return new ActionParameter(new ActionParameterKey(actionKey, name), value);

    }

}
