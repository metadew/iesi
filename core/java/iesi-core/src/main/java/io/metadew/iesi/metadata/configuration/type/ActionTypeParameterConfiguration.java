package io.metadew.iesi.metadata.configuration.type;

import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.action.ActionType;
import io.metadew.iesi.metadata.definition.action.ActionTypeParameter;

public class ActionTypeParameterConfiguration {

    private ActionTypeParameter actionTypeParameter;

    // Constructors
    public ActionTypeParameterConfiguration(ActionTypeParameter actionTypeParameter) {
        this.setActionTypeParameter(actionTypeParameter);
    }

    public ActionTypeParameterConfiguration() {
    }

    // Get Action Type Parameter
    public ActionTypeParameter getActionTypeParameter(String actionTypeName, String actionTypeParameterName) {
        ActionTypeParameter actionTypeParameterResult = null;
        ActionTypeConfiguration actionTypeConfiguration = new ActionTypeConfiguration();
        ActionType actionType = actionTypeConfiguration.getActionType(actionTypeName);
        for (ActionTypeParameter actionTypeParameter : actionType.getParameters()) {
            if (actionTypeParameter.getName().equalsIgnoreCase(actionTypeParameterName.toLowerCase())) {
                actionTypeParameterResult = actionTypeParameter;
                break;
            }
        }
        return actionTypeParameterResult;
    }

    // Getters and Setters
    public ActionTypeParameter getActionTypeParameter() {
        return actionTypeParameter;
    }

    public void setActionTypeParameter(ActionTypeParameter actionTypeParameter) {
        this.actionTypeParameter = actionTypeParameter;
    }

}