package io.metadew.iesi.server.rest.actiontypes;

import io.metadew.iesi.metadata.definition.action.type.ActionTypeParameter;
import org.springframework.stereotype.Service;

@Service
public class ActionTypeParameterDtoService implements IActionTypeParameterDtoService {

    public ActionTypeParameterDto convertToDto(ActionTypeParameter actionTypeParameter, String name) {
        return new ActionTypeParameterDto(name, actionTypeParameter.getDescription(), actionTypeParameter.getType(),
                actionTypeParameter.isMandatory(), actionTypeParameter.isEncrypted());
    }

}
