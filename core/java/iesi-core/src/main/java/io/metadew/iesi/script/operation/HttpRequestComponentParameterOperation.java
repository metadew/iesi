package io.metadew.iesi.script.operation;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeService;
import io.metadew.iesi.framework.configuration.FrameworkFolderConfiguration;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.ComponentAttribute;
import io.metadew.iesi.metadata.definition.ComponentParameter;
import io.metadew.iesi.runtime.definition.LookupResult;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import org.apache.logging.log4j.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Operation that manages the parameters for http requests that have been defined as components.
 *
 * @author peter.billen
 */
public class HttpRequestComponentParameterOperation {

    private final DataTypeService dataTypeService;
    private FrameworkExecution frameworkExecution;
    private ExecutionControl executionControl;
    private ActionExecution actionExecution;
    private List<ComponentAttribute> attributes;
    private String name;
    private String value = "";
    private String inputValue = "";

    public HttpRequestComponentParameterOperation(ExecutionControl executionControl) {
        this.executionControl = executionControl;
        this.dataTypeService = new DataTypeService(executionControl.getExecutionRuntime());

    }

    private DataType getParameterValue(String value, List<ComponentAttribute> componentAttributes, ActionExecution actionExecution) {
        // Resolve attributes
        value = executionControl.getExecutionRuntime().resolveComponentTypeVariables(value, componentAttributes, executionControl.getEnvName());
        // Resolve concept lookups
        // TODO: newly added variable resolvement, should be
        value = executionControl.getExecutionRuntime().resolveVariables(actionExecution, value);
        value = this.getExecutionControl().getExecutionRuntime().resolveConceptLookup(executionControl,
                value, true).getValue();
        value = executionControl.getExecutionRuntime().resolveVariables(actionExecution, value);
        // Resolve internal encryption
        value = FrameworkCrypto.getInstance().resolve(actionExecution.getFrameworkExecution(), value);
        return dataTypeService.resolve(value);
    }

    public DataType getParameterValue(ComponentParameter componentParameter, List<ComponentAttribute> componentAttributes, ActionExecution actionExecution) {
        executionControl.logMessage(this.getActionExecution(), "component.param " + this.getName() + ": " + componentParameter.getValue(), Level.DEBUG);
        return getParameterValue(componentParameter.getValue(), componentAttributes, actionExecution);
    }

    public boolean isHeader(ComponentParameter componentParameter) {
        return componentParameter.getName().startsWith("header");
    }

    public boolean isQueryParameter(ComponentParameter componentParameter) {
        return componentParameter.getName().startsWith("queryparam");
    }

    public Map<String, DataType> getHeader(ComponentParameter componentParameter, List<ComponentAttribute> componentAttributes, ActionExecution actionExecution) {
        Map<String, DataType> header = new HashMap<>();
        if (isHeader(componentParameter)) {
            header.put(componentParameter.getValue().split(",")[0],
                    getParameterValue(componentParameter.getValue().split(",")[1], componentAttributes, actionExecution));
            return header;
        } else {
            throw new RuntimeException();
        }
    }

    public Map<String, DataType> getQueryParameter(ComponentParameter componentParameter, List<ComponentAttribute> componentAttributes, ActionExecution actionExecution) {
        Map<String, DataType> header = new HashMap<>();
        if (isQueryParameter(componentParameter)) {
            header.put(componentParameter.getValue().split(",")[0],
                    getParameterValue(componentParameter.getValue().split(",")[1], componentAttributes, actionExecution));
            return header;
        } else {
            throw new RuntimeException();
        }
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
        String decryptedValue = FrameworkCrypto.getInstance().resolve(this.getFrameworkExecution(),
                this.getValue());

        this.setValue(decryptedValue);

    }

    public ExecutionControl getExecutionControl() {
        return executionControl;
    }

    public void setExecutionControl(ExecutionControl executionControl) {
        this.executionControl = executionControl;
    }

    public ActionExecution getActionExecution() {
        return actionExecution;
    }

    public void setActionExecution(ActionExecution actionExecution) {
        this.actionExecution = actionExecution;
    }

    public List<ComponentAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<ComponentAttribute> attributes) {
        this.attributes = attributes;
    }

}