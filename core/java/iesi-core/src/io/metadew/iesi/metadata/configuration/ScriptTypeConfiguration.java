package io.metadew.iesi.metadata.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.ScriptType;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.metadata.operation.TypeConfigurationOperation;

public class ScriptTypeConfiguration {

    private ScriptType scriptType;
    private FrameworkExecution frameworkExecution;
    private String dataObjectType = "ScriptType";

    // Constructors
    public ScriptTypeConfiguration(ScriptType scriptType, FrameworkExecution frameworkExecution) {
        this.setScriptType(scriptType);
        this.setFrameworkExecution(frameworkExecution);
    }

    public ScriptTypeConfiguration(FrameworkExecution frameworkExecution) {
        this.setFrameworkExecution(frameworkExecution);
    }

    // Methods
    public ScriptType getScriptType(String scriptTypeName) {
        String conf = TypeConfigurationOperation.getTypeConfigurationFile(this.getFrameworkExecution(),
                this.getDataObjectType(), scriptTypeName);
        DataObjectOperation dataObjectOperation = new DataObjectOperation(this.getFrameworkExecution(), conf);
        ObjectMapper objectMapper = new ObjectMapper();
        ScriptType scriptType = objectMapper.convertValue(dataObjectOperation.getDataObject().getData(),
                ScriptType.class);
        return scriptType;
    }

    // Getters and Setters
    public ScriptType getScriptType() {
        return scriptType;
    }

    public void setScriptType(ScriptType scriptType) {
        this.scriptType = scriptType;
    }

    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

    public String getDataObjectType() {
        return dataObjectType;
    }

    public void setDataObjectType(String dataObjectType) {
        this.dataObjectType = dataObjectType;
    }

}