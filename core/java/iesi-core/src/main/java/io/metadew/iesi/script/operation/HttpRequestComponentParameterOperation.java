package io.metadew.iesi.script.operation;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeResolver;
import io.metadew.iesi.datatypes.Text;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.type.ComponentTypeParameterConfiguration;
import io.metadew.iesi.metadata.definition.ComponentAttribute;
import io.metadew.iesi.metadata.definition.ComponentParameter;
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
public class HttpRequestComponentParameterOperation {

    private ComponentTypeParameterConfiguration componentTypeParameterConfiguration;
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
    public HttpRequestComponentParameterOperation(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
                                                  ActionExecution actionExecution, List<ComponentAttribute> attributes, String name) {
        this.setFrameworkExecution(frameworkExecution);
        this.setExecutionControl(executionControl);
        this.setActionExecution(actionExecution);
        this.setAttributes(attributes);
        this.setName(name);
        this.lookupComponentTypeParameter();
    }

    public HttpRequestComponentParameterOperation(FrameworkExecution frameworkExecution, ExecutionControl executionControl,
                                                  ActionExecution actionExecution, List<ComponentAttribute> attributes, String name, String value) {
        this.setFrameworkExecution(frameworkExecution);
        this.setExecutionControl(executionControl);
        this.setActionExecution(actionExecution);
        this.setAttributes(attributes);
        this.setName(name);
        this.lookupComponentTypeParameter();

        this.setInputValue(value);
    }

    public HttpRequestComponentParameterOperation(ExecutionControl executionControl, ActionExecution actionExecution) {
        this.executionControl = executionControl;
        this.actionExecution = actionExecution;
        this.componentTypeParameterConfiguration = new ComponentTypeParameterConfiguration(executionControl.getFrameworkExecution().getFrameworkInstance());
    }

    public HttpRequestComponentParameterOperation(ExecutionControl executionControl) {
        this.executionControl = executionControl;
        this.componentTypeParameterConfiguration = new ComponentTypeParameterConfiguration(executionControl.getFrameworkExecution().getFrameworkInstance());

    }

    public DataType getParameterValue(ComponentParameter componentParameter, List<ComponentAttribute> componentAttributes, ActionExecution actionExecution) {
        ComponentTypeParameter componentTypeParameter = componentTypeParameterConfiguration.getComponentTypeParameter("http.request", componentParameter.getName());
        executionControl.logMessage(this.getActionExecution(), "component.param " + this.getName() + ": " + componentParameter.getValue(), Level.DEBUG);

        String value = componentParameter.getValue();
        // Resolve attributes
        value = executionControl.getExecutionRuntime().resolveComponentTypeVariables(value, componentAttributes, executionControl.getEnvName());
        // Resolve concept lookups
        // TODO: newly added variable resolvement, should be
        value = executionControl.getExecutionRuntime().resolveVariables(actionExecution, value);
        value = this.getExecutionControl().getExecutionRuntime().resolveConceptLookup(executionControl,
                value, true).getValue();
        value = executionControl.getExecutionRuntime().resolveVariables(actionExecution, value);
        // Resolve internal encryption
        value = executionControl.getFrameworkExecution().getFrameworkCrypto().resolve(this.getFrameworkExecution(), value);
        return DataTypeResolver.resolveToDataType(value, frameworkExecution.getFrameworkConfiguration().getFolderConfiguration(), executionControl.getExecutionRuntime());
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