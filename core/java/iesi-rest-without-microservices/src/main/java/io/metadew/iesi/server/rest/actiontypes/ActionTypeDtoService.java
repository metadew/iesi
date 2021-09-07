package io.metadew.iesi.server.rest.actiontypes;

import io.metadew.iesi.metadata.definition.action.type.ActionType;
import io.metadew.iesi.server.rest.actiontypes.parameter.ActionTypeParameterDtoService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@ConditionalOnWebApplication
public class ActionTypeDtoService implements IActionTypeDtoService {

    private ActionTypeParameterDtoService actionTypeParameterDtoService;

    public ActionTypeDtoService(ActionTypeParameterDtoService actionTypeParameterDtoService) {
        this.actionTypeParameterDtoService = actionTypeParameterDtoService;
    }

    public ActionTypeDto convertToDto(ActionType actionType, String name) {
        return new ActionTypeDto(name, actionType.getDescription(), actionType.getStatus(),
                actionType.getParameters().entrySet().stream()
                        .map(entry -> actionTypeParameterDtoService.convertToDto(entry.getValue(), entry.getKey()))
                        .collect(Collectors.toList()));
    }

}
