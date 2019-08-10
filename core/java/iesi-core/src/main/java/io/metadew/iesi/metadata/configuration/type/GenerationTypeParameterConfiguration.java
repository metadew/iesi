package io.metadew.iesi.metadata.configuration.type;

import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.definition.GenerationType;
import io.metadew.iesi.metadata.definition.GenerationTypeParameter;

public class GenerationTypeParameterConfiguration {

    private GenerationTypeParameter generationTypeParameter;

    // Constructors
    public GenerationTypeParameterConfiguration(GenerationTypeParameter generationTypeParameter) {
        this.setgenerationTypeParameter(generationTypeParameter);
    }

    public GenerationTypeParameterConfiguration(FrameworkInstance frameworkInstance) {
    }

    // Get Generation Type Parameter
    public GenerationTypeParameter getGenerationTypeParameter(String GenerationTypeName, String GenerationTypeParameterName) {
        GenerationTypeParameter GenerationTypeParameterResult = null;
        GenerationTypeConfiguration GenerationTypeConfiguration = new GenerationTypeConfiguration();
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

}