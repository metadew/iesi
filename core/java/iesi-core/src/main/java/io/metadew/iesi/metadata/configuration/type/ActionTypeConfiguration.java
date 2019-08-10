package io.metadew.iesi.metadata.configuration.type;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.metadata.definition.action.ActionType;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.metadata.operation.TypeConfigurationOperation;

public class ActionTypeConfiguration {

    private ActionType actionType;
    private String dataObjectType = "ActionType";

    // Constructors
    public ActionTypeConfiguration(ActionType actionType) {
        this.setActionType(actionType);
    }

    public ActionTypeConfiguration() {
    }

    public ActionType getActionType(String actionTypeName) {
        String conf = TypeConfigurationOperation.getTypeConfigurationFile(this.getDataObjectType(), actionTypeName);
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

}