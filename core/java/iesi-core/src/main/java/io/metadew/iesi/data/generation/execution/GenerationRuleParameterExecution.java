package io.metadew.iesi.data.generation.execution;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.type.GenerationRuleTypeParameterConfiguration;
import io.metadew.iesi.metadata.definition.GenerationRuleTypeParameter;
import io.metadew.iesi.script.execution.ExecutionControl;
import org.apache.logging.log4j.Level;


public class GenerationRuleParameterExecution {

    private FrameworkExecution frameworkExecution;
    private ExecutionControl executionControl;
    private String generationRuleTypeName;
    private String name;
    private String value = "";
    private String inputValue = "";

    private GenerationRuleTypeParameter generationRuleTypeParameter;

    // Constructors
    public GenerationRuleParameterExecution(FrameworkExecution frameworkExecution, ExecutionControl executionControl, String generationRuleTypeName, String name) {
        this.setFrameworkExecution(frameworkExecution);
        this.setExecutionControl(executionControl);
        this.setGenerationRuleTypeName(generationRuleTypeName);
        this.setName(name);
        this.lookupGenerationRuleTypeParameter();
    }

    public GenerationRuleParameterExecution(FrameworkExecution frameworkExecution, ExecutionControl executionControl, String generationRuleTypeName, String name,
                                            String value) {
        this.setFrameworkExecution(frameworkExecution);
        this.setExecutionControl(executionControl);
        this.setGenerationRuleTypeName(generationRuleTypeName);
        this.setName(name);
        this.lookupGenerationRuleTypeParameter();

        this.setInputValue(value);
    }

    // Methods
    private void lookupGenerationRuleTypeParameter() {
        GenerationRuleTypeParameterConfiguration generationRuleTypeParameterConfiguration = new GenerationRuleTypeParameterConfiguration(this.getFrameworkExecution().getFrameworkInstance());
        this.setGenerationRuleTypeParameter(generationRuleTypeParameterConfiguration.getGenerationRuleTypeParameter(this.getGenerationRuleTypeName(), this.getName()));
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

    public String getGenerationRuleTypeName() {
        return generationRuleTypeName;
    }

    public void setGenerationRuleTypeName(String generationRuleTypeName) {
        this.generationRuleTypeName = generationRuleTypeName;
    }

    public String getInputValue() {
        return inputValue;
    }

    public void setInputValue(String inputValue) {
        this.inputValue = inputValue;
        this.setValue(inputValue);

        this.getFrameworkExecution().getFrameworkLog().log("generation.rule.parameter.set." + this.getName() + "=" + this.getValue(),
                Level.DEBUG);
    }

    public GenerationRuleTypeParameter getGenerationRuleTypeParameter() {
        return generationRuleTypeParameter;
    }

    public void setGenerationRuleTypeParameter(GenerationRuleTypeParameter generationRuleTypeParameter) {
        this.generationRuleTypeParameter = generationRuleTypeParameter;
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