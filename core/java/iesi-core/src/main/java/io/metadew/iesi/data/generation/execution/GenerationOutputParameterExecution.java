package io.metadew.iesi.data.generation.execution;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.type.GenerationOutputTypeParameterConfiguration;
import io.metadew.iesi.metadata.definition.GenerationOutputTypeParameter;
import io.metadew.iesi.script.execution.ExecutionControl;
import org.apache.logging.log4j.Level;

public class GenerationOutputParameterExecution {

    private FrameworkExecution frameworkExecution;
    private ExecutionControl executionControl;
    private String generationOutputTypeName;
    private String name;
    private String value = "";
    private String inputValue = "";

    private GenerationOutputTypeParameter generationOutputTypeParameter;

    // Constructors
    public GenerationOutputParameterExecution(FrameworkExecution frameworkExecution, ExecutionControl executionControl, String generationOutputTypeName, String name) {
        this.setFrameworkExecution(frameworkExecution);
        this.setExecutionControl(executionControl);
        this.setGenerationOutputTypeName(generationOutputTypeName);
        this.setName(name);
        this.lookupGenerationOutputTypeParameter();
    }

    public GenerationOutputParameterExecution(FrameworkExecution frameworkExecution, ExecutionControl executionControl, String generationOutputTypeName, String name,
                                              String value) {
        this.setFrameworkExecution(frameworkExecution);
        this.setExecutionControl(executionControl);
        this.setGenerationOutputTypeName(generationOutputTypeName);
        this.setName(name);
        this.lookupGenerationOutputTypeParameter();

        this.setInputValue(value);
    }

    // Methods
    private void lookupGenerationOutputTypeParameter() {
        GenerationOutputTypeParameterConfiguration generationOutputTypeParameterConfiguration = new GenerationOutputTypeParameterConfiguration(this.getFrameworkExecution().getFrameworkInstance());
        this.setGenerationOutputTypeParameter(generationOutputTypeParameterConfiguration.getGenerationOutputTypeParameter(this.getGenerationOutputTypeName(), this.getName()));
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = this.getExecutionControl().getExecutionRuntime().resolveVariables(value);
    }

    public String getGenerationOutputTypeName() {
        return generationOutputTypeName;
    }

    public void setGenerationOutputTypeName(String generationOutputTypeName) {
        this.generationOutputTypeName = generationOutputTypeName;
    }

    public String getInputValue() {
        return inputValue;
    }

    public void setInputValue(String inputValue) {
        this.inputValue = inputValue;
        this.setValue(inputValue);

        this.getFrameworkExecution().getFrameworkLog().log("generation.output.parameter.set." + this.getName() + "=" + this.getValue(),
                Level.DEBUG);
    }

    public GenerationOutputTypeParameter getGenerationOutputTypeParameter() {
        return generationOutputTypeParameter;
    }

    public void setGenerationOutputTypeParameter(GenerationOutputTypeParameter generationOutputTypeParameter) {
        this.generationOutputTypeParameter = generationOutputTypeParameter;
    }

    public ExecutionControl getExecutionControl() {
        return executionControl;
    }

    public void setExecutionControl(ExecutionControl executionControl) {
        this.executionControl = executionControl;
    }

    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

}