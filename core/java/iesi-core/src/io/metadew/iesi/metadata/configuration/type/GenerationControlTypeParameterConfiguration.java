package io.metadew.iesi.metadata.configuration.type;

import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.GenerationControlType;
import io.metadew.iesi.metadata.definition.GenerationControlTypeParameter;

public class GenerationControlTypeParameterConfiguration {

    private GenerationControlTypeParameter generationControlTypeParameter;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public GenerationControlTypeParameterConfiguration(GenerationControlTypeParameter generationControlTypeParameter, FrameworkInstance frameworkInstance) {
        this.setgenerationControlTypeParameter(generationControlTypeParameter);
        this.setFrameworkInstance(frameworkInstance);
    }

    public GenerationControlTypeParameterConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    // Get GenerationControl Type Parameter
    public GenerationControlTypeParameter getGenerationControlTypeParameter(String GenerationControlTypeName, String GenerationControlTypeParameterName) {
        GenerationControlTypeParameter GenerationControlTypeParameterResult = null;
        GenerationControlTypeConfiguration GenerationControlTypeConfiguration = new GenerationControlTypeConfiguration(this.getFrameworkInstance());
        GenerationControlType GenerationControlType = GenerationControlTypeConfiguration.getGenerationControlType(GenerationControlTypeName);
        for (GenerationControlTypeParameter GenerationControlTypeParameter : GenerationControlType.getParameters()) {
            if (GenerationControlTypeParameter.getName().equalsIgnoreCase(GenerationControlTypeParameterName)) {
                GenerationControlTypeParameterResult = GenerationControlTypeParameter;
                break;
            }
        }
        return GenerationControlTypeParameterResult;
    }

    // Getters and Setters
    public GenerationControlTypeParameter getgenerationControlTypeParameter() {
        return generationControlTypeParameter;
    }

    public void setgenerationControlTypeParameter(GenerationControlTypeParameter generationControlTypeParameter) {
        this.generationControlTypeParameter = generationControlTypeParameter;
    }

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}