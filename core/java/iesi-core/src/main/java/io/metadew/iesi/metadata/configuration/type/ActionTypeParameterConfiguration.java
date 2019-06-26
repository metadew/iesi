package io.metadew.iesi.metadata.configuration.type;

import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.ActionType;
import io.metadew.iesi.metadata.definition.ActionTypeParameter;

public class ActionTypeParameterConfiguration {

    private ActionTypeParameter actionTypeParameter;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public ActionTypeParameterConfiguration(ActionTypeParameter actionTypeParameter, FrameworkInstance frameworkInstance) {
        this.setActionTypeParameter(actionTypeParameter);
        this.setFrameworkInstance(frameworkInstance);
    }

    public ActionTypeParameterConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    // Get Action Type Parameter
    public ActionTypeParameter getActionTypeParameter(String actionTypeName, String actionTypeParameterName) {
        ActionTypeParameter actionTypeParameterResult = null;
        ActionTypeConfiguration actionTypeConfiguration = new ActionTypeConfiguration(this.getFrameworkInstance());
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

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}


}