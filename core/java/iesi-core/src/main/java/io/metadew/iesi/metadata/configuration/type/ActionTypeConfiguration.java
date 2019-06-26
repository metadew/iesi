package io.metadew.iesi.metadata.configuration.type;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.ActionType;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.metadata.operation.TypeConfigurationOperation;

public class ActionTypeConfiguration {

    private ActionType actionType;
    private FrameworkInstance frameworkInstance;
    private String dataObjectType = "ActionType";

    // Constructors
    public ActionTypeConfiguration(ActionType actionType, FrameworkInstance frameworkInstance) {
        this.setActionType(actionType);
        this.setFrameworkInstance(frameworkInstance);
    }

    public ActionTypeConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    public ActionType getActionType(String actionTypeName) {
        String conf = TypeConfigurationOperation.getTypeConfigurationFile(this.getFrameworkInstance(), this.getDataObjectType(), actionTypeName);
        DataObjectOperation dataObjectOperation = new DataObjectOperation(conf);
        ObjectMapper objectMapper = new ObjectMapper();
        ActionType actionType = objectMapper.convertValue(dataObjectOperation.getDataObject().getData(),
                ActionType.class);
        return actionType;
    }

    // Getters and Setters
    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public String getDataObjectType() {
        return dataObjectType;
    }

    public void setDataObjectType(String dataObjectType) {
        this.dataObjectType = dataObjectType;
    }

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}