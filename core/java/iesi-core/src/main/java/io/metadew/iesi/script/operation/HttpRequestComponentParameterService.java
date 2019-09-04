package io.metadew.iesi.script.operation;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeService;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;
import io.metadew.iesi.metadata.definition.component.ComponentAttribute;
import io.metadew.iesi.metadata.definition.component.ComponentParameter;
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
public class HttpRequestComponentParameterService {

    private final DataTypeService dataTypeService;
    private ExecutionControl executionControl;

    public HttpRequestComponentParameterService(ExecutionControl executionControl) {
        this.executionControl = executionControl;
        this.dataTypeService = new DataTypeService();
    }

    private DataType getParameterValue(String value, List<ComponentAttribute> componentAttributes, ActionExecution actionExecution) {
        // Resolve attributes
        value = executionControl.getExecutionRuntime().resolveComponentTypeVariables(value, componentAttributes, executionControl.getEnvName());
        // Resolve concept lookups
        // TODO: newly added variable resolvement, should be
        value = executionControl.getExecutionRuntime().resolveVariables(actionExecution, value);
        value = this.getExecutionControl().getExecutionRuntime().resolveConceptLookup(
                value).getValue();
        value = executionControl.getExecutionRuntime().resolveVariables(actionExecution, value);
        // Resolve internal encryption
        value = FrameworkCrypto.getInstance().resolve(value);
        return dataTypeService.resolve(value);
    }

    public DataType getParameterValue(ComponentParameter componentParameter, List<ComponentAttribute> componentAttributes, ActionExecution actionExecution) {
        executionControl.logMessage(actionExecution, "component.param " + componentParameter.getName() + ": " + componentParameter.getValue(), Level.DEBUG);
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

    public ExecutionControl getExecutionControl() {
        return executionControl;
    }

    public void setExecutionControl(ExecutionControl executionControl) {
        this.executionControl = executionControl;
    }

}