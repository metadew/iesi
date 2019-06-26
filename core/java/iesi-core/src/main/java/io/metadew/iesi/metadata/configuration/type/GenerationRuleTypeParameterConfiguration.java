package io.metadew.iesi.metadata.configuration.type;

import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.GenerationRuleType;
import io.metadew.iesi.metadata.definition.GenerationRuleTypeParameter;

public class GenerationRuleTypeParameterConfiguration {

    private GenerationRuleTypeParameter generationRuleTypeParameter;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public GenerationRuleTypeParameterConfiguration(GenerationRuleTypeParameter generationRuleTypeParameter, FrameworkInstance frameworkInstance) {
        this.setgenerationRuleTypeParameter(generationRuleTypeParameter);
        this.setFrameworkInstance(frameworkInstance);
    }

    public GenerationRuleTypeParameterConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    // Get GenerationRule Type Parameter
    public GenerationRuleTypeParameter getGenerationRuleTypeParameter(String GenerationRuleTypeName, String GenerationRuleTypeParameterName) {
        GenerationRuleTypeParameter GenerationRuleTypeParameterResult = null;
        GenerationRuleTypeConfiguration GenerationRuleTypeConfiguration = new GenerationRuleTypeConfiguration(this.getFrameworkInstance());
        GenerationRuleType GenerationRuleType = GenerationRuleTypeConfiguration.getGenerationRuleType(GenerationRuleTypeName);
        for (GenerationRuleTypeParameter GenerationRuleTypeParameter : GenerationRuleType.getParameters()) {
            if (GenerationRuleTypeParameter.getName().equalsIgnoreCase(GenerationRuleTypeParameterName)) {
                GenerationRuleTypeParameterResult = GenerationRuleTypeParameter;
                break;
            }
        }
        return GenerationRuleTypeParameterResult;
    }

    // Getters and Setters
    public GenerationRuleTypeParameter getgenerationRuleTypeParameter() {
        return generationRuleTypeParameter;
    }

    public void setgenerationRuleTypeParameter(GenerationRuleTypeParameter generationRuleTypeParameter) {
        this.generationRuleTypeParameter = generationRuleTypeParameter;
    }

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}