package io.metadew.iesi.metadata.configuration.type;

import io.metadew.iesi.common.configuration.metadata.actiontypes.MetadataActionTypesConfiguration;
import io.metadew.iesi.metadata.definition.action.type.ActionTypeParameter;

import java.util.Optional;

public class ActionTypeParameterConfiguration {

    public ActionTypeParameterConfiguration() {
    }

    // Get Action Type Parameter
    public Optional<ActionTypeParameter> getActionTypeParameter(String actionTypeName, String actionTypeParameterName) {
        return Optional.ofNullable(MetadataActionTypesConfiguration.getInstance().getActionType(actionTypeName)
                .orElseThrow(() -> new RuntimeException("action type " + actionTypeName + " not found"))
                .getParameters().get(actionTypeParameterName));
    }

}