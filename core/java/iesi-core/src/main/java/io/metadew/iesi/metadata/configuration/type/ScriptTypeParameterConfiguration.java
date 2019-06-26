package io.metadew.iesi.metadata.configuration.type;

import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.ScriptType;
import io.metadew.iesi.metadata.definition.ScriptTypeParameter;

public class ScriptTypeParameterConfiguration {

    private ScriptTypeParameter scriptTypeParameter;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public ScriptTypeParameterConfiguration(ScriptTypeParameter scriptTypeParameter, FrameworkInstance frameworkInstance) {
        this.setScriptTypeParameter(scriptTypeParameter);
        this.setFrameworkInstance(frameworkInstance);
    }

    public ScriptTypeParameterConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    // Get Script Type Parameter
    public ScriptTypeParameter getScriptTypeParameter(String scriptTypeName, String scriptTypeParameterName) {
        ScriptTypeParameter scriptTypeParameterResult = null;
        ScriptTypeConfiguration scriptTypeConfiguration = new ScriptTypeConfiguration(this.getFrameworkInstance());
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

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}