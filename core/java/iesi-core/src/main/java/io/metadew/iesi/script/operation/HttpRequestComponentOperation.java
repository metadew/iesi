package io.metadew.iesi.script.operation;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.ComponentConfiguration;
import io.metadew.iesi.metadata.definition.Action;
import io.metadew.iesi.metadata.definition.Component;
import io.metadew.iesi.metadata.definition.ComponentParameter;
import io.metadew.iesi.metadata.definition.HttpRequestComponent;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;

import javax.xml.crypto.Data;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Operation that manages http requests that have been defined as components.
 *
 * @author peter.billen
 */
public class HttpRequestComponentOperation {

    private HttpRequestComponentParameterOperation httpRequestComponentParameterOperation;
    private ComponentConfiguration componentConfiguration;
    private FrameworkExecution frameworkExecution;

    private ExecutionControl executionControl;

    private ActionExecution actionExecution;

    private String requestName;

    private Component request;

    // parameters
    private HttpRequestComponentParameterOperation url;

    private HashMap<String, HttpRequestComponentParameterOperation> headerMap;

    private HashMap<String, HttpRequestComponentParameterOperation> queryParamMap;

    private HashMap<String, HttpRequestComponentParameterOperation> requestParameterOperationMap;

    // Constructors
    public HttpRequestComponentOperation(FrameworkExecution frameworkExecution, ExecutionControl executionControl, ActionExecution actionExecution,
                                         String requestName) {
        this.frameworkExecution = frameworkExecution;
        this.executionControl = executionControl;
        this.actionExecution = actionExecution;
        this.requestParameterOperationMap = new HashMap<>();
        this.requestName = requestName;
        this.getRequestConfiguration();
    }

    public HttpRequestComponentOperation(ExecutionControl executionControl, ActionExecution actionExecution,
                                         String requestName) {
        this.frameworkExecution = executionControl.getFrameworkExecution();
        this.executionControl = executionControl;
        this.actionExecution = actionExecution;
        this.requestParameterOperationMap = new HashMap<>();
        this.requestName = requestName;
        this.getRequestConfiguration();
    }


    public HttpRequestComponentOperation(ExecutionControl executionControl, ActionExecution actionExecution) {
        this.executionControl = executionControl;
        this.actionExecution = actionExecution;
        this.componentConfiguration = new ComponentConfiguration(executionControl.getFrameworkExecution().getFrameworkInstance());
        this.requestParameterOperationMap = new HashMap<>();
        this.getRequestConfiguration();
        this.httpRequestComponentParameterOperation = new HttpRequestComponentParameterOperation(executionControl);
    }

    public HttpRequestComponentOperation(ExecutionControl executionControl) {
        this.executionControl = executionControl;
        this.componentConfiguration = new ComponentConfiguration(executionControl.getFrameworkExecution().getFrameworkInstance());
        this.httpRequestComponentParameterOperation = new HttpRequestComponentParameterOperation(executionControl);
    }

    public HttpRequestComponent getHttpRequestComponent(String requestComponentName, ActionExecution actionExecution) {
        Component request = componentConfiguration.getComponent(this.getRequestName())
                .orElseThrow(() -> new RuntimeException(MessageFormat.format("component.notfound=no component exists with name {0}.", getRequestName())));

        if (request.getType().equalsIgnoreCase("http.request")) {
            throw new RuntimeException(MessageFormat.format("Component ''http.request'' not of type but type {0}", request.getType()));
        }

        DataType uri = request.getParameters().stream()
                .filter(componentParameter -> componentParameter.getName().equalsIgnoreCase("url"))
                .findFirst()
                .map(componentParameter -> httpRequestComponentParameterOperation.getParameterValue(componentParameter, request.getAttributes(), actionExecution))
                .orElseThrow(() -> new RuntimeException("No url defined in http request"));

        Map<String, DataType> headers = request.getParameters().stream()
                .filter(componentParameter -> componentParameter.getName().startsWith("header"))
                .collect(Collectors.toMap(ComponentParameter::getName,
                        componentParameter -> httpRequestComponentParameterOperation.getParameterValue(componentParameter, request.getAttributes(), actionExecution)));

        Map<String, DataType> queryParameters = request.getParameters().stream()
                .filter(componentParameter -> componentParameter.getName().startsWith("queryparam"))
                .collect(Collectors.toMap(ComponentParameter::getName,
                        componentParameter -> httpRequestComponentParameterOperation.getParameterValue(componentParameter, request.getAttributes(), actionExecution)));

        return new HttpRequestComponent(uri, headers, queryParameters);
    }


    private void getRequestConfiguration() {
        ComponentConfiguration componentConfiguration = new ComponentConfiguration(this.getFrameworkExecution().getFrameworkInstance());
        Component request = componentConfiguration.getComponent(this.getRequestName())
                .orElseThrow(() -> new RuntimeException(MessageFormat.format("component.notfound=no component exists with name {0}.", getRequestName())));
        this.setRequest(request);

        // Reset parameters
        this.setUrl(new HttpRequestComponentParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(), this.getActionExecution(),
                this.getRequest().getAttributes(), "url"));
        this.setHeaderMap(new HashMap<>());
        this.setQueryParamMap(new HashMap<>());

        // Get Parameters
        for (ComponentParameter componentParameter : this.getRequest().getParameters()) {
            if (componentParameter.getName().equalsIgnoreCase("url")) {
                this.getUrl().setInputValue(componentParameter.getValue());
            } else if (componentParameter.getName().toLowerCase().startsWith("header")) {
                HttpRequestComponentParameterOperation httpRequestComponentParameterOperation = new HttpRequestComponentParameterOperation(this.getFrameworkExecution(),
                        this.getExecutionControl(), this.getActionExecution(), this.getRequest().getAttributes(),
                        componentParameter.getName());
                httpRequestComponentParameterOperation.setInputValue(componentParameter.getValue());
                this.getHeaderMap().put(componentParameter.getName(), httpRequestComponentParameterOperation);
            } else if (componentParameter.getName().toLowerCase().startsWith("queryparam")) {
                HttpRequestComponentParameterOperation httpRequestComponentParameterOperation = new HttpRequestComponentParameterOperation(this.getFrameworkExecution(),
                        this.getExecutionControl(), this.getActionExecution(), this.getRequest().getAttributes(),
                        componentParameter.getName());
                httpRequestComponentParameterOperation.setInputValue(componentParameter.getValue());
                this.getQueryParamMap().put(componentParameter.getName(), httpRequestComponentParameterOperation);
            }
        }

        // Create parameter list
        this.getRequestParameterOperationMap().put("url", this.getUrl());

    }

    // Getters and setters
    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

    public String getRequestName() {
        return requestName;
    }

    public void setRequestName(String requestName) {
        this.requestName = requestName;
    }

    public Component getRequest() {
        return request;
    }

    public void setRequest(Component request) {
        this.request = request;
    }

    public HttpRequestComponentParameterOperation getUrl() {
        return url;
    }

    public void setUrl(HttpRequestComponentParameterOperation url) {
        this.url = url;
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

    public HashMap<String, HttpRequestComponentParameterOperation> getRequestParameterOperationMap() {
        return requestParameterOperationMap;
    }

    public void setRequestParameterOperationMap(HashMap<String, HttpRequestComponentParameterOperation> requestParameterOperationMap) {
        this.requestParameterOperationMap = requestParameterOperationMap;
    }

    public HashMap<String, HttpRequestComponentParameterOperation> getHeaderMap() {
        return headerMap;
    }

    public void setHeaderMap(HashMap<String, HttpRequestComponentParameterOperation> headerMap) {
        this.headerMap = headerMap;
    }

    public HashMap<String, HttpRequestComponentParameterOperation> getQueryParamMap() {
        return queryParamMap;
    }

    public void setQueryParamMap(HashMap<String, HttpRequestComponentParameterOperation> queryParamMap) {
        this.queryParamMap = queryParamMap;
    }

}