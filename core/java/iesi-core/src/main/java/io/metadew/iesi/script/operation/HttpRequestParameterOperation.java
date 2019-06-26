package io.metadew.iesi.script.operation;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.type.ComponentTypeParameterConfiguration;
import io.metadew.iesi.metadata.definition.ComponentAttribute;
import io.metadew.iesi.metadata.definition.ComponentTypeParameter;
import io.metadew.iesi.runtime.definition.LookupResult;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import org.apache.logging.log4j.Level;

import java.util.List;

/**
 * Operation that manages the parameters for http requests that have been defined as components.
 *
 * @author peter.billen
 */
public class HttpRequestParameterOperation {

    private FrameworkExecution frameworkExecution;
    private ExecutionControl executionControl;
    private ActionExecution actionExecution;
    private List<ComponentAttribute> attributes;
    private String name;
    private String value = "";
    private String inputValue = "";

    private ComponentTypeParameter componentTypeParameter;
    private SubroutineOperation subroutineOperation;

    // Constructors
    public HttpRequestParameterOperation(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
                                     ActionExecution actionExecution, List<ComponentAttribute> attributes, String name) {
        this.setFrameworkExecution(frameworkExecution);
        this.setExecutionControl(executionControl);
        this.setActionExecution(actionExecution);
        this.setAttributes(attributes);
        this.setName(name);
        this.lookupComponentTypeParameter();
    }

    public HttpRequestParameterOperation(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
                                     ActionExecution actionExecution, List<ComponentAttribute> attributes, String name, String value) {
        this.setFrameworkExecution(frameworkExecution);
        this.setExecutionControl(executionControl);
        this.setActionExecution(actionExecution);
        this.setAttributes(attributes);
        this.setName(name);
        this.lookupComponentTypeParameter();

        this.setInputValue(value);
    }

    // Methods
    private void lookupComponentTypeParameter() {
        ComponentTypeParameterConfiguration componentTypeParameterConfiguration = new ComponentTypeParameterConfiguration(
                this.getFrameworkExecution().getFrameworkInstance());
        this.setComponentTypeParameter(
                componentTypeParameterConfiguration.getComponentTypeParameter("http.request", this.getName()));
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
        this.value = this.getExecutionControl().getExecutionRuntime().resolveVariables(this.getActionExecution(),
                value);
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

        this.getExecutionControl().logMessage(this.getActionExecution(),
                "PARAM " + this.getName() + ": " + this.getValue(), Level.DEBUG);

        // Lookup attributes
        this.setValue(this.getExecutionControl().getExecutionRuntime().resolveComponentTypeVariables(this.getValue(), this.getAttributes(), this.getExecutionControl().getEnvName()));

        // Cross concept lookup
        LookupResult lookupResult = this.getExecutionControl().getExecutionRuntime().resolveConceptLookup(this.getExecutionControl(),
                this.getValue(), true);
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

    public SubroutineOperation getSubroutineOperation() {
        return subroutineOperation;
    }

    public void setSubroutineOperation(SubroutineOperation subroutineOperation) {
        this.subroutineOperation = subroutineOperation;
    }

    public ActionExecution getActionExecution() {
        return actionExecution;
    }

    public void setActionExecution(ActionExecution actionExecution) {
        this.actionExecution = actionExecution;
    }

    public ComponentTypeParameter getComponentTypeParameter() {
        return componentTypeParameter;
    }

    public void setComponentTypeParameter(ComponentTypeParameter componentTypeParameter) {
        this.componentTypeParameter = componentTypeParameter;
    }

    public List<ComponentAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<ComponentAttribute> attributes) {
        this.attributes = attributes;
    }

}