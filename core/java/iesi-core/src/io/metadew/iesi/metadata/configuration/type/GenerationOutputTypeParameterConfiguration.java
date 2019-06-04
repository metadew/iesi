package io.metadew.iesi.metadata.configuration.type;

import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.GenerationOutputType;
import io.metadew.iesi.metadata.definition.GenerationOutputTypeParameter;

public class GenerationOutputTypeParameterConfiguration {

    private GenerationOutputTypeParameter generationOutputTypeParameter;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public GenerationOutputTypeParameterConfiguration(GenerationOutputTypeParameter generationOutputTypeParameter, FrameworkInstance frameworkInstance) {
        this.setgenerationOutputTypeParameter(generationOutputTypeParameter);
        this.setFrameworkInstance(frameworkInstance);
    }

    public GenerationOutputTypeParameterConfiguration(FrameworkInstance frameworkInstance) {
    	this.setFrameworkInstance(frameworkInstance);
    }

    // Get GenerationOutput Type Parameter
    public GenerationOutputTypeParameter getGenerationOutputTypeParameter(String GenerationOutputTypeName, String GenerationOutputTypeParameterName) {
        GenerationOutputTypeParameter GenerationOutputTypeParameterResult = null;
        GenerationOutputTypeConfiguration GenerationOutputTypeConfiguration = new GenerationOutputTypeConfiguration(this.getFrameworkInstance());
        GenerationOutputType GenerationOutputType = GenerationOutputTypeConfiguration.getGenerationOutputType(GenerationOutputTypeName);
        for (GenerationOutputTypeParameter GenerationOutputTypeParameter : GenerationOutputType.getParameters()) {
            if (GenerationOutputTypeParameter.getName().equalsIgnoreCase(GenerationOutputTypeParameterName)) {
                GenerationOutputTypeParameterResult = GenerationOutputTypeParameter;
                break;
            }
        }
        return GenerationOutputTypeParameterResult;
    }

    // Getters and Setters
    public GenerationOutputTypeParameter getgenerationOutputTypeParameter() {
        return generationOutputTypeParameter;
    }

    public void setgenerationOutputTypeParameter(GenerationOutputTypeParameter generationOutputTypeParameter) {
        this.generationOutputTypeParameter = generationOutputTypeParameter;
    }

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}