package io.metadew.iesi.metadata.configuration.type;

import io.metadew.iesi.metadata.definition.generation.GenerationControlType;
import io.metadew.iesi.metadata.definition.generation.GenerationControlTypeParameter;

public class GenerationControlTypeParameterConfiguration {

    private GenerationControlTypeParameter generationControlTypeParameter;

    // Constructors
    public GenerationControlTypeParameterConfiguration(GenerationControlTypeParameter generationControlTypeParameter) {
        this.setgenerationControlTypeParameter(generationControlTypeParameter);
    }

    public GenerationControlTypeParameterConfiguration() {
    }

    // Get GenerationControl Type Parameter
    public GenerationControlTypeParameter getGenerationControlTypeParameter(String GenerationControlTypeName, String GenerationControlTypeParameterName) {
        GenerationControlTypeParameter GenerationControlTypeParameterResult = null;
        GenerationControlTypeConfiguration GenerationControlTypeConfiguration = new GenerationControlTypeConfiguration();
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

}