package io.metadew.iesi.data.generation.execution;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.type.GenerationControlRuleTypeParameterConfiguration;
import io.metadew.iesi.metadata.definition.GenerationControlRuleTypeParameter;
import io.metadew.iesi.script.execution.ExecutionControl;
import org.apache.logging.log4j.Level;

public class GenerationControlRuleParameterExecution {

    private FrameworkExecution frameworkExecution;
    private ExecutionControl executionControl;
    private String generationControlRuleTypeName;
    private String name;
    private String value = "";
    private String inputValue = "";

    private GenerationControlRuleTypeParameter generationControlRuleTypeParameter;

    // Constructors
    public GenerationControlRuleParameterExecution(FrameworkExecution frameworkExecution, ExecutionControl executionControl, String generationControlRuleTypeName, String name) {
        this.setFrameworkExecution(frameworkExecution);
        this.setExecutionControl(executionControl);
        this.setGenerationControlRuleTypeName(generationControlRuleTypeName);
        this.setName(name);
        this.lookupGenerationControlRuleTypeParameter();
    }

    public GenerationControlRuleParameterExecution(FrameworkExecution frameworkExecution, ExecutionControl executionControl, String generationControlRuleTypeName, String name,
                                                   String value) {
        this.setFrameworkExecution(frameworkExecution);
        this.setExecutionControl(executionControl);
        this.setGenerationControlRuleTypeName(generationControlRuleTypeName);
        this.setName(name);
        this.lookupGenerationControlRuleTypeParameter();

        this.setInputValue(value);
    }

    // Methods
    private void lookupGenerationControlRuleTypeParameter() {
        GenerationControlRuleTypeParameterConfiguration generationControlRuleTypeParameterConfiguration = new GenerationControlRuleTypeParameterConfiguration(this.getFrameworkExecution().getFrameworkInstance());
        this.setGenerationControlRuleTypeParameter(generationControlRuleTypeParameterConfiguration.getGenerationControlRuleTypeParameter(this.getGenerationControlRuleTypeName(), this.getName()));
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

    public String getInputValue() {
        return inputValue;
    }

    public void setInputValue(String inputValue) {
        this.inputValue = inputValue;
        this.setValue(inputValue);

        this.getFrameworkExecution().getFrameworkLog().log("generation.control.parameter.set." + this.getName() + "=" + this.getValue(),
                Level.DEBUG);
    }

    public String getGenerationControlRuleTypeName() {
        return generationControlRuleTypeName;
    }

    public void setGenerationControlRuleTypeName(String generationControlRuleTypeName) {
        this.generationControlRuleTypeName = generationControlRuleTypeName;
    }

    public GenerationControlRuleTypeParameter getGenerationControlRuleTypeParameter() {
        return generationControlRuleTypeParameter;
    }

    public void setGenerationControlRuleTypeParameter(GenerationControlRuleTypeParameter generationControlRuleTypeParameter) {
        this.generationControlRuleTypeParameter = generationControlRuleTypeParameter;
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