package io.metadew.iesi.script.operation;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.runtime.definition.LookupResult;
import io.metadew.iesi.script.execution.ExecutionControl;

public class RepositoryParameterOperation {

    private FrameworkExecution frameworkExecution;
    private ExecutionControl executionControl;
    private String name;
    private String value = "";
    private String inputValue = "";

    // Constructors
    public RepositoryParameterOperation(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
                                        String name) {
        this.setFrameworkExecution(frameworkExecution);
        this.setExecutionControl(executionControl);
        this.setName(name);
    }

    public RepositoryParameterOperation(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
                                        String name, String value) {
        this.setFrameworkExecution(frameworkExecution);
        this.setExecutionControl(executionControl);
        this.setName(name);

        this.setInputValue(value);
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
        this.value = this.getExecutionControl().getExecutionRuntime().resolveVariables(value, true);
    }

    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

    public String getInputValue() {
        return inputValue;
    }

    public void setInputValue(String inputValue) {
        this.inputValue = inputValue;
        this.setValue(inputValue);

        // this.getExecutionControl().logMessage(this.getActionExecution(),"PARAM " +
        // this.getName() + ": " + this.getValue(), Level.DEBUG);

        // Cross concept lookup
        LookupResult lookupResult = this.getExecutionControl().getExecutionRuntime()
                .resolveConceptLookup(this.getExecutionControl(), this.getValue(), true);
        this.setValue(lookupResult.getValue());

        // Resolve internal encryption
        String decryptedValue = this.getFrameworkExecution().getFrameworkCrypto().resolve(this.getFrameworkExecution(),
                this.getValue());

        this.setValue(decryptedValue);

    }

    public ExecutionControl getExecutionControl() {
        return executionControl;
    }

    public void setExecutionControl(ExecutionControl executionControl) {
        this.executionControl = executionControl;
    }

}