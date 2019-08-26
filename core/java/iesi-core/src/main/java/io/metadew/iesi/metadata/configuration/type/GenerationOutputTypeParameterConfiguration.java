package io.metadew.iesi.metadata.configuration.type;

import io.metadew.iesi.metadata.definition.generation.GenerationOutputType;
import io.metadew.iesi.metadata.definition.generation.GenerationOutputTypeParameter;

public class GenerationOutputTypeParameterConfiguration {

    private GenerationOutputTypeParameter generationOutputTypeParameter;

    // Constructors
    public GenerationOutputTypeParameterConfiguration(GenerationOutputTypeParameter generationOutputTypeParameter) {
        this.setgenerationOutputTypeParameter(generationOutputTypeParameter);
    }

    public GenerationOutputTypeParameterConfiguration() {
    }

    // Get GenerationOutput Type Parameter
    public GenerationOutputTypeParameter getGenerationOutputTypeParameter(String GenerationOutputTypeName, String GenerationOutputTypeParameterName) {
        GenerationOutputTypeParameter GenerationOutputTypeParameterResult = null;
        GenerationOutputTypeConfiguration GenerationOutputTypeConfiguration = new GenerationOutputTypeConfiguration();
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

}