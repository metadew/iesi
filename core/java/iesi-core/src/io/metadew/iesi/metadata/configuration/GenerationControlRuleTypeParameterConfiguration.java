package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.GenerationControlRuleType;
import io.metadew.iesi.metadata.definition.GenerationControlRuleTypeParameter;

public class GenerationControlRuleTypeParameterConfiguration {

    private GenerationControlRuleTypeParameter generationControlRuleTypeParameter;
    private FrameworkExecution frameworkExecution;

    // Constructors
    public GenerationControlRuleTypeParameterConfiguration(GenerationControlRuleTypeParameter generationControlRuleTypeParameter, FrameworkExecution processiongTools) {
        this.setgenerationControlRuleTypeParameter(generationControlRuleTypeParameter);
        this.setFrameworkExecution(processiongTools);
    }

    public GenerationControlRuleTypeParameterConfiguration(FrameworkExecution processiongTools) {
        this.setFrameworkExecution(processiongTools);
    }

    // Get GenerationControlRule Type Parameter
    public GenerationControlRuleTypeParameter getGenerationControlRuleTypeParameter(String GenerationControlRuleTypeName, String GenerationControlRuleTypeParameterName) {
        GenerationControlRuleTypeParameter GenerationControlRuleTypeParameterResult = null;
        GenerationControlRuleTypeConfiguration GenerationControlRuleTypeConfiguration = new GenerationControlRuleTypeConfiguration(this.getFrameworkExecution());
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

    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

}