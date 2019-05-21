package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.ScriptType;
import io.metadew.iesi.metadata.definition.ScriptTypeParameter;

public class ScriptTypeParameterConfiguration {

    private ScriptTypeParameter scriptTypeParameter;
    private FrameworkExecution frameworkExecution;

    // Constructors
    public ScriptTypeParameterConfiguration(ScriptTypeParameter scriptTypeParameter, FrameworkExecution processiongTools) {
        this.setScriptTypeParameter(scriptTypeParameter);
        this.setFrameworkExecution(processiongTools);
    }

    public ScriptTypeParameterConfiguration(FrameworkExecution processiongTools) {
        this.setFrameworkExecution(processiongTools);
    }

    // Get Script Type Parameter
    public ScriptTypeParameter getScriptTypeParameter(String scriptTypeName, String scriptTypeParameterName) {
        ScriptTypeParameter scriptTypeParameterResult = null;
        ScriptTypeConfiguration scriptTypeConfiguration = new ScriptTypeConfiguration(this.getFrameworkExecution());
        ScriptType scriptType = scriptTypeConfiguration.getScriptType(scriptTypeName);
        for (ScriptTypeParameter scriptTypeParameter : scriptType.getParameters()) {
            if (scriptTypeParameter.getName().equalsIgnoreCase(scriptTypeParameterName)) {
                scriptTypeParameterResult = scriptTypeParameter;
                break;
            }
        }
        return scriptTypeParameterResult;
    }

    // Getters and Setters
    public ScriptTypeParameter getScriptTypeParameter() {
        return scriptTypeParameter;
    }

    public void setScriptTypeParameter(ScriptTypeParameter scriptTypeParameter) {
        this.scriptTypeParameter = scriptTypeParameter;
    }

    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

}