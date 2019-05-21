package io.metadew.iesi.metadata.configuration;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.ActionType;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.metadata.operation.TypeConfigurationOperation;

public class ActionTypeConfiguration {

    private ActionType actionType;
    private FrameworkExecution frameworkExecution;
    private String dataObjectType = "ActionType";

    // Constructors
    public ActionTypeConfiguration(ActionType actionType, FrameworkExecution frameworkExecution) {
        this.setActionType(actionType);
        this.setFrameworkExecution(frameworkExecution);
    }

    public ActionTypeConfiguration(FrameworkExecution frameworkExecution) {
        this.setFrameworkExecution(frameworkExecution);
    }

    public ActionType getActionType(String actionTypeName) {
        String conf = TypeConfigurationOperation.getTypeConfigurationFile(this.getFrameworkExecution(), this.getDataObjectType(), actionTypeName);
        DataObjectOperation dataObjectOperation = new DataObjectOperation(this.getFrameworkExecution(), conf);
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

    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

}