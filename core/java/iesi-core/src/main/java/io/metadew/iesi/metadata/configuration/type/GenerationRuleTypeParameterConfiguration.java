package io.metadew.iesi.metadata.configuration.type;

import io.metadew.iesi.metadata.definition.GenerationRuleType;
import io.metadew.iesi.metadata.definition.GenerationRuleTypeParameter;

public class GenerationRuleTypeParameterConfiguration {

    private GenerationRuleTypeParameter generationRuleTypeParameter;

    // Constructors
    public GenerationRuleTypeParameterConfiguration(GenerationRuleTypeParameter generationRuleTypeParameter) {
        this.setgenerationRuleTypeParameter(generationRuleTypeParameter);
    }

    public GenerationRuleTypeParameterConfiguration() {
    }

    // Get GenerationRule Type Parameter
    public GenerationRuleTypeParameter getGenerationRuleTypeParameter(String GenerationRuleTypeName, String GenerationRuleTypeParameterName) {
        GenerationRuleTypeParameter GenerationRuleTypeParameterResult = null;
        GenerationRuleTypeConfiguration GenerationRuleTypeConfiguration = new GenerationRuleTypeConfiguration();
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

}