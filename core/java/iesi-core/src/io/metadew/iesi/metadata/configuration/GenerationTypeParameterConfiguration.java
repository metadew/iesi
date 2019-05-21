package io.metadew.iesi.metadata.configuration;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.GenerationType;
import io.metadew.iesi.metadata.definition.GenerationTypeParameter;

public class GenerationTypeParameterConfiguration {

    private GenerationTypeParameter generationTypeParameter;
    private FrameworkExecution frameworkExecution;

    // Constructors
    public GenerationTypeParameterConfiguration(GenerationTypeParameter generationTypeParameter, FrameworkExecution processiongTools) {
        this.setgenerationTypeParameter(generationTypeParameter);
        this.setFrameworkExecution(processiongTools);
    }

    public GenerationTypeParameterConfiguration(FrameworkExecution processiongTools) {
        this.setFrameworkExecution(processiongTools);
    }

    // Get Generation Type Parameter
    public GenerationTypeParameter getGenerationTypeParameter(String GenerationTypeName, String GenerationTypeParameterName) {
        GenerationTypeParameter GenerationTypeParameterResult = null;
        GenerationTypeConfiguration GenerationTypeConfiguration = new GenerationTypeConfiguration(this.getFrameworkExecution());
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

    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

}