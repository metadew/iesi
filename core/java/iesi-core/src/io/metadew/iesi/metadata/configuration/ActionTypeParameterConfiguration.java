package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.ActionType;
import io.metadew.iesi.metadata.definition.ActionTypeParameter;

public class ActionTypeParameterConfiguration {

	private ActionTypeParameter actionTypeParameter;
	private FrameworkExecution frameworkExecution;

	// Constructors
	public ActionTypeParameterConfiguration(ActionTypeParameter actionTypeParameter, FrameworkExecution processiongTools) {
		this.setActionTypeParameter(actionTypeParameter);
		this.setFrameworkExecution(processiongTools);
	}

	public ActionTypeParameterConfiguration(FrameworkExecution processiongTools) {
		this.setFrameworkExecution(processiongTools);
	}

	// Get Action Type Parameter
	public ActionTypeParameter getActionTypeParameter(String actionTypeName, String actionTypeParameterName) {
		ActionTypeParameter actionTypeParameterResult = null;
		ActionTypeConfiguration actionTypeConfiguration = new ActionTypeConfiguration(this.getFrameworkExecution());
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

	public FrameworkExecution getFrameworkExecution() {
		return frameworkExecution;
	}

	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
		this.frameworkExecution = frameworkExecution;
	}
	

}