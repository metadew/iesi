package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.GenerationRuleType;
import io.metadew.iesi.metadata.definition.GenerationRuleTypeParameter;

public class GenerationRuleTypeParameterConfiguration {

    private GenerationRuleTypeParameter generationRuleTypeParameter;
    private FrameworkExecution frameworkExecution;

    // Constructors
    public GenerationRuleTypeParameterConfiguration(GenerationRuleTypeParameter generationRuleTypeParameter, FrameworkExecution processiongTools) {
        this.setgenerationRuleTypeParameter(generationRuleTypeParameter);
        this.setFrameworkExecution(processiongTools);
    }

    public GenerationRuleTypeParameterConfiguration(FrameworkExecution processiongTools) {
        this.setFrameworkExecution(processiongTools);
    }

    // Get GenerationRule Type Parameter
    public GenerationRuleTypeParameter getGenerationRuleTypeParameter(String GenerationRuleTypeName, String GenerationRuleTypeParameterName) {
        GenerationRuleTypeParameter GenerationRuleTypeParameterResult = null;
        GenerationRuleTypeConfiguration GenerationRuleTypeConfiguration = new GenerationRuleTypeConfiguration(this.getFrameworkExecution());
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

    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

}