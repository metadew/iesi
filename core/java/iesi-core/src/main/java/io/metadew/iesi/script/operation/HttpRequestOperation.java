package io.metadew.iesi.script.operation;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.ComponentConfiguration;
import io.metadew.iesi.metadata.definition.Component;
import io.metadew.iesi.metadata.definition.ComponentParameter;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;

import java.text.MessageFormat;
import java.util.HashMap;

/**
 * Operation that manages http requests that have been defined as components.
 *
 * @author peter.billen
 */
public class HttpRequestOperation {

    private FrameworkExecution frameworkExecution;

    private ExecutionControl executionControl;

    private ActionExecution actionExecution;

    private String requestName;

    private Component request;

    // parameters
    private HttpRequestParameterOperation url;

    private HashMap<String, HttpRequestParameterOperation> headerMap;

    private HashMap<String, HttpRequestParameterOperation> queryParamMap;

    private HashMap<String, HttpRequestParameterOperation> requestParameterOperationMap;

    // Constructors
    public HttpRequestOperation(FrameworkExecution frameworkExecution, ExecutionControl executionControl, ActionExecution actionExecution,
                            String requestName) {
        this.setFrameworkExecution(frameworkExecution);
        this.setExecutionControl(executionControl);
        this.setActionExecution(actionExecution);
        this.setRequestParameterOperationMap(new HashMap<String, HttpRequestParameterOperation>());
        this.setRequestName(requestName);
        this.getRequestConfiguration();
    }

    private void getRequestConfiguration() {
        ComponentConfiguration componentConfiguration = new ComponentConfiguration(this.getFrameworkExecution().getFrameworkInstance());
        Component request = componentConfiguration.getComponent(this.getRequestName())
                .orElseThrow(() -> new RuntimeException(MessageFormat.format("component.notfound=no component exists with name {0}.", getRequestName())));
        this.setRequest(request);

        // Reset parameters
        this.setUrl(new HttpRequestParameterOperation(this.getFrameworkExecution(), this.getExecutionControl(), this.getActionExecution(),
                this.getRequest().getAttributes(), "url"));
        this.setHeaderMap(new HashMap<String, HttpRequestParameterOperation>());
        this.setQueryParamMap(new HashMap<String, HttpRequestParameterOperation>());

        // Get Parameters
        for (ComponentParameter componentParameter : this.getRequest().getParameters()) {
            if (componentParameter.getName().equalsIgnoreCase("url")) {
                this.getUrl().setInputValue(componentParameter.getValue());
            } else if (componentParameter.getName().toLowerCase().startsWith("header")) {
                HttpRequestParameterOperation httpRequestParameterOperation = new HttpRequestParameterOperation(this.getFrameworkExecution(),
                        this.getExecutionControl(), this.getActionExecution(), this.getRequest().getAttributes(),
                        componentParameter.getName());
                httpRequestParameterOperation.setInputValue(componentParameter.getValue());
                this.getHeaderMap().put(componentParameter.getName(), httpRequestParameterOperation);
            } else if (componentParameter.getName().toLowerCase().startsWith("queryparam")) {
                HttpRequestParameterOperation httpRequestParameterOperation = new HttpRequestParameterOperation(this.getFrameworkExecution(),
                        this.getExecutionControl(), this.getActionExecution(), this.getRequest().getAttributes(),
                        componentParameter.getName());
                httpRequestParameterOperation.setInputValue(componentParameter.getValue());
                this.getQueryParamMap().put(componentParameter.getName(), httpRequestParameterOperation);
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

    public HttpRequestParameterOperation getUrl() {
        return url;
    }

    public void setUrl(HttpRequestParameterOperation url) {
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

    public HashMap<String, HttpRequestParameterOperation> getRequestParameterOperationMap() {
        return requestParameterOperationMap;
    }

    public void setRequestParameterOperationMap(HashMap<String, HttpRequestParameterOperation> requestParameterOperationMap) {
        this.requestParameterOperationMap = requestParameterOperationMap;
    }

    public HashMap<String, HttpRequestParameterOperation> getHeaderMap() {
        return headerMap;
    }

    public void setHeaderMap(HashMap<String, HttpRequestParameterOperation> headerMap) {
        this.headerMap = headerMap;
    }

    public HashMap<String, HttpRequestParameterOperation> getQueryParamMap() {
        return queryParamMap;
    }

    public void setQueryParamMap(HashMap<String, HttpRequestParameterOperation> queryParamMap) {
        this.queryParamMap = queryParamMap;
    }

}