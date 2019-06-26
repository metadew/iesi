package io.metadew.iesi.metadata.configuration.type;

import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.GenerationType;
import io.metadew.iesi.metadata.definition.GenerationTypeParameter;

public class GenerationTypeParameterConfiguration {

    private GenerationTypeParameter generationTypeParameter;
    private FrameworkInstance frameworkInstance;

    // Constructors
    public GenerationTypeParameterConfiguration(GenerationTypeParameter generationTypeParameter, FrameworkInstance frameworkInstance) {
        this.setgenerationTypeParameter(generationTypeParameter);
        this.setFrameworkInstance(frameworkInstance);
    }

    public GenerationTypeParameterConfiguration(FrameworkInstance frameworkInstance) {
        this.setFrameworkInstance(frameworkInstance);
    }

    // Get Generation Type Parameter
    public GenerationTypeParameter getGenerationTypeParameter(String GenerationTypeName, String GenerationTypeParameterName) {
        GenerationTypeParameter GenerationTypeParameterResult = null;
        GenerationTypeConfiguration GenerationTypeConfiguration = new GenerationTypeConfiguration(this.getFrameworkInstance());
        GenerationType GenerationType = GenerationTypeConfiguration.getGenerationType(GenerationTypeName);
        for (GenerationTypeParameter GenerationTypeParameter : GenerationType.getParameters()) {
            if (GenerationTypeParameter.getName().equalsIgnoreCase(GenerationTypeParameterName)) {
                GenerationTypeParameterResult = GenerationTypeParameter;
                break;
            }
        }
        return GenerationTypeParameterResult;
    }


    // Getters and Setters
    public GenerationTypeParameter getgenerationTypeParameter() {
        return generationTypeParameter;
    }

    public void setgenerationTypeParameter(GenerationTypeParameter generationTypeParameter) {
        this.generationTypeParameter = generationTypeParameter;
    }

	public FrameworkInstance getFrameworkInstance() {
		return frameworkInstance;
	}

	public void setFrameworkInstance(FrameworkInstance frameworkInstance) {
		this.frameworkInstance = frameworkInstance;
	}

}