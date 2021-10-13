package io.metadew.iesi.server.rest.actiontypes.parameter;

import io.metadew.iesi.metadata.definition.action.type.ActionTypeParameter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnWebApplication
public class ActionTypeParameterDtoService implements IActionTypeParameterDtoService {

    public ActionTypeParameterDto convertToDto(ActionTypeParameter actionTypeParameter, String name) {
        return new ActionTypeParameterDto(name, actionTypeParameter.getDescription(), actionTypeParameter.getType(),
                actionTypeParameter.isMandatory(), actionTypeParameter.isEncrypted());
    }

}
