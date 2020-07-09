package io.metadew.iesi.metadata.service;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.metadata.definition.component.ComponentAttribute;
import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import org.apache.logging.log4j.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequestComponentParameterService {


    private static HttpRequestComponentParameterService INSTANCE;

    public synchronized static HttpRequestComponentParameterService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HttpRequestComponentParameterService();
        }
        return INSTANCE;
    }

    private HttpRequestComponentParameterService() {
    }

    private DataType getParameterValue(String value, List<ComponentAttribute> componentAttributes, ActionExecution actionExecution, ExecutionControl executionControl) throws Exception {
        // Resolve attributes
        value = executionControl.getExecutionRuntime().resolveComponentTypeVariables(value, componentAttributes, executionControl.getEnvName());
        // Resolve concept lookups
        // TODO: newly added variable resolvement, should be
        value = executionControl.getExecutionRuntime().resolveVariables(actionExecution, value);
        value = executionControl.getExecutionRuntime().resolveConceptLookup(value).getValue();
        value = executionControl.getExecutionRuntime().resolveVariables(actionExecution, value);
        // Resolve internal encryption
        value = FrameworkCrypto.getInstance().resolve(value);
        return DataTypeHandler.getInstance().resolve(value, executionControl.getExecutionRuntime());
    }

    public DataType getParameterValue(ComponentParameter componentParameter, List<ComponentAttribute> componentAttributes, ActionExecution actionExecution, ExecutionControl executionControl) throws Exception {
        executionControl.logMessage("component.param " + componentParameter.getMetadataKey().getParameterName() + ": " + componentParameter.getValue(), Level.DEBUG);
        return getParameterValue(componentParameter.getValue(), componentAttributes, actionExecution, executionControl);
    }

    public boolean isHeader(ComponentParameter componentParameter) {
        return componentParameter.getMetadataKey().getParameterName().startsWith("header");
    }

    public boolean isQueryParameter(ComponentParameter componentParameter) {
        return componentParameter.getMetadataKey().getParameterName().startsWith("queryparam");
    }

    public Map<String, DataType> getHeader(ComponentParameter componentParameter, List<ComponentAttribute> componentAttributes, ActionExecution actionExecution, ExecutionControl executionControl) throws Exception {
        Map<String, DataType> header = new HashMap<>();
        if (isHeader(componentParameter)) {
            header.put(componentParameter.getValue().split(",", 2)[0],
                    getParameterValue(componentParameter.getValue().split(",", 2)[1], componentAttributes, actionExecution, executionControl));
            return header;
        } else {
            throw new RuntimeException();
        }
    }

    public Map<String, DataType> getQueryParameter(ComponentParameter componentParameter, List<ComponentAttribute> componentAttributes, ActionExecution actionExecution, ExecutionControl executionControl) throws Exception {
        Map<String, DataType> header = new HashMap<>();
        if (isQueryParameter(componentParameter)) {
            header.put(componentParameter.getValue().split(",", 2)[0],
                    getParameterValue(componentParameter.getValue().split(",", 2)[1], componentAttributes, actionExecution, executionControl));
            return header;
        } else {
            throw new RuntimeException();
        }
    }

}