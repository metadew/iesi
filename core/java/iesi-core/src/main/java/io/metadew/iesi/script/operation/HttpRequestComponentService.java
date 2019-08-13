package io.metadew.iesi.script.operation;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.ComponentConfiguration;
import io.metadew.iesi.metadata.definition.Component;
import io.metadew.iesi.metadata.definition.HttpRequestComponent;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;

import java.text.MessageFormat;
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

    private Component request;

    public HttpRequestComponentOperation(ExecutionControl executionControl) {
        this.executionControl = executionControl;
        this.componentConfiguration = new ComponentConfiguration();
        this.httpRequestComponentParameterOperation = new HttpRequestComponentParameterOperation(executionControl);
    }

    public HttpRequestComponent getHttpRequestComponent(String requestComponentName, ActionExecution actionExecution) {
        Component request = componentConfiguration.getComponent(requestComponentName)
                .orElseThrow(() -> new RuntimeException(MessageFormat.format("component.notfound=no component exists with name {0}.", requestComponentName)));

        if (!request.getType().equalsIgnoreCase("http.request")) {
            throw new RuntimeException(MessageFormat.format("Component ''http.request'' not of type 'http.request' but type {0}", request.getType()));
        }

        DataType uri = request.getParameters().stream()
                .filter(componentParameter -> componentParameter.getName().equalsIgnoreCase("url"))
                .findFirst()
                .map(componentParameter -> httpRequestComponentParameterOperation.getParameterValue(componentParameter, request.getAttributes(), actionExecution))
                .orElseThrow(() -> new RuntimeException("No url defined in http request"));

        Map<String, DataType> headers = request.getParameters().stream()
                .filter(componentParameter -> componentParameter.getName().startsWith("header"))
                .map(componentParameter -> httpRequestComponentParameterOperation.getHeader(componentParameter, request.getAttributes(), actionExecution))
                .flatMap(m->m.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Map<String, DataType> queryParameters = request.getParameters().stream()
                .filter(componentParameter -> componentParameter.getName().startsWith("queryparam"))
                .map(componentParameter -> httpRequestComponentParameterOperation.getQueryParameter(componentParameter, request.getAttributes(), actionExecution))
                .flatMap(m->m.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return new HttpRequestComponent(uri, headers, queryParameters);
    }


    // Getters and setters
    public FrameworkExecution getFrameworkExecution() {
        return frameworkExecution;
    }

    public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
        this.frameworkExecution = frameworkExecution;
    }

    public Component getRequest() {
        return request;
    }

    public void setRequest(Component request) {
        this.request = request;
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

}