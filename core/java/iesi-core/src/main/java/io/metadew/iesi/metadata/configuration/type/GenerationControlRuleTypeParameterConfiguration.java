package io.metadew.iesi.metadata.configuration.type;

import io.metadew.iesi.metadata.definition.generation.GenerationControlRuleType;
import io.metadew.iesi.metadata.definition.generation.GenerationControlRuleTypeParameter;

public class GenerationControlRuleTypeParameterConfiguration {

    private GenerationControlRuleTypeParameter generationControlRuleTypeParameter;

    // Constructors
    public GenerationControlRuleTypeParameterConfiguration(GenerationControlRuleTypeParameter generationControlRuleTypeParameter) {
        this.setgenerationControlRuleTypeParameter(generationControlRuleTypeParameter);
    }

    public GenerationControlRuleTypeParameterConfiguration() {
    }

    // Get GenerationControlRule Type Parameter
    public GenerationControlRuleTypeParameter getGenerationControlRuleTypeParameter(String GenerationControlRuleTypeName, String GenerationControlRuleTypeParameterName) {
        GenerationControlRuleTypeParameter GenerationControlRuleTypeParameterResult = null;
        GenerationControlRuleTypeConfiguration GenerationControlRuleTypeConfiguration = new GenerationControlRuleTypeConfiguration();
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

}