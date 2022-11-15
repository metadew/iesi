package io.metadew.iesi.metadata.configuration.type;

import io.metadew.iesi.common.configuration.metadata.actiontypes.MetadataActionTypesConfiguration;
import io.metadew.iesi.metadata.definition.action.type.ActionTypeParameter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class ActionTypeParameterConfiguration {

    private final MetadataActionTypesConfiguration metadataActionTypesConfiguration;

    public ActionTypeParameterConfiguration(MetadataActionTypesConfiguration metadataActionTypesConfiguration) {
        this.metadataActionTypesConfiguration = metadataActionTypesConfiguration;
    }

    // Get Action Type Parameter
    public Optional<ActionTypeParameter> getActionTypeParameter(String actionTypeName, String actionTypeParameterName) {
        return Optional.ofNullable(metadataActionTypesConfiguration.getActionType(actionTypeName)
                .orElseThrow(() -> new RuntimeException("action type " + actionTypeName + " not found"))
                .getParameters().get(actionTypeParameterName));
    }


    // Get Action Type Parameter
    public Map<String, ActionTypeParameter> getActionTypeParameters(String actionTypeName) {
        return metadataActionTypesConfiguration.getActionType(actionTypeName)
                .orElseThrow(() -> new RuntimeException("action type " + actionTypeName + " not found"))
                .getParameters();
    }

}