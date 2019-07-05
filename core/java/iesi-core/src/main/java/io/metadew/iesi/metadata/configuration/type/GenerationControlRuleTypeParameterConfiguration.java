package io.metadew.iesi.metadata.configuration.type;

import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.GenerationControlRuleType;
import io.metadew.iesi.metadata.definition.GenerationControlRuleTypeParameter;

public class GenerationControlRuleTypeParameterConfiguration {

    private GenerationControlRuleTypeParameter generationControlRuleTypeParameter;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public GenerationControlRuleTypeParameterConfiguration(GenerationControlRuleTypeParameter generationControlRuleTypeParameter, FrameworkInstance frameworkInstance) {
        this.setgenerationControlRuleTypeParameter(generationControlRuleTypeParameter);
        this.setFrameworkInstance(frameworkInstance);
    }

    public GenerationControlRuleTypeParameterConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    // Get GenerationControlRule Type Parameter
    public GenerationControlRuleTypeParameter getGenerationControlRuleTypeParameter(String GenerationControlRuleTypeName, String GenerationControlRuleTypeParameterName) {
        GenerationControlRuleTypeParameter GenerationControlRuleTypeParameterResult = null;
        GenerationControlRuleTypeConfiguration GenerationControlRuleTypeConfiguration = new GenerationControlRuleTypeConfiguration(this.getFrameworkInstance());
        GenerationControlRuleType GenerationControlRuleType = GenerationControlRuleTypeConfiguration.getGenerationControlRuleType(GenerationControlRuleTypeName);
        for (GenerationControlRuleTypeParameter GenerationControlRuleTypeParameter : GenerationControlRuleType.getParameters()) {
            if (GenerationControlRuleTypeParameter.getName().equalsIgnoreCase(GenerationControlRuleTypeParameterName)) {
                GenerationControlRuleTypeParameterResult = GenerationControlRuleTypeParameter;
                break;
            }
        }
        return GenerationControlRuleTypeParameterResult;
    }

    // Getters and Setters
    public GenerationControlRuleTypeParameter getgenerationControlRuleTypeParameter() {
        return generationControlRuleTypeParameter;
    }

    public void setgenerationControlRuleTypeParameter(GenerationControlRuleTypeParameter generationControlRuleTypeParameter) {
        this.generationControlRuleTypeParameter = generationControlRuleTypeParameter;
    }

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}