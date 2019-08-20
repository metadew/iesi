package io.metadew.iesi.metadata.configuration.type;

import io.metadew.iesi.metadata.definition.script.type.ScriptType;
import io.metadew.iesi.metadata.definition.script.type.ScriptTypeParameter;

public class ScriptTypeParameterConfiguration {

    private ScriptTypeParameter scriptTypeParameter;

    // Constructors
    public ScriptTypeParameterConfiguration(ScriptTypeParameter scriptTypeParameter) {
        this.setScriptTypeParameter(scriptTypeParameter);
    }

    public ScriptTypeParameterConfiguration() {
    }

    // Get Script Type Parameter
    public ScriptTypeParameter getScriptTypeParameter(String scriptTypeName, String scriptTypeParameterName) {
        ScriptTypeParameter scriptTypeParameterResult = null;
        ScriptTypeConfiguration scriptTypeConfiguration = new ScriptTypeConfiguration();
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
}