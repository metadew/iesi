package io.metadew.iesi.metadata.configuration.type;

import io.metadew.iesi.metadata.definition.action.type.ActionType;
import io.metadew.iesi.metadata.definition.action.type.ActionTypeParameter;

import java.util.Optional;

public class ActionTypeParameterConfiguration {

    private final ActionTypeConfiguration actionTypeConfiguration;

    public ActionTypeParameterConfiguration() {
        actionTypeConfiguration = new ActionTypeConfiguration();
    }

    // Get Action Type Parameter
    public Optional<ActionTypeParameter> getActionTypeParameter(String actionTypeName, String actionTypeParameterName) {
        ActionType actionType = actionTypeConfiguration.getActionType(actionTypeName);
        for (ActionTypeParameter actionTypeParameter : actionType.getParameters()) {
            if (actionTypeParameter.getName().equalsIgnoreCase(actionTypeParameterName.toLowerCase())) {
                return Optional.of(actionTypeParameter);
            }
        }
        return Optional.empty();
    }

}