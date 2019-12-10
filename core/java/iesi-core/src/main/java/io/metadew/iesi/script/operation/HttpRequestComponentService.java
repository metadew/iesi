package io.metadew.iesi.script.operation;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.metadata.configuration.component.ComponentConfiguration;
import io.metadew.iesi.metadata.configuration.exception.ComponentDoesNotExistException;
import io.metadew.iesi.metadata.definition.HttpRequestComponent;
import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Operation that manages http requests that have been defined as components.
 *
 * @author peter.billen
 */
public class HttpRequestComponentService {

    private HttpRequestComponentParameterService httpRequestComponentParameterService;
    private ExecutionControl executionControl;

    public HttpRequestComponentService(ExecutionControl executionControl) {
        this.executionControl = executionControl;
        this.httpRequestComponentParameterService = new HttpRequestComponentParameterService(executionControl);
    }

    public HttpRequestComponent getHttpRequestComponent(String requestComponentName, ActionExecution actionExecution) throws ComponentDoesNotExistException, SQLException {
        Component request = ComponentConfiguration.getInstance().get(requestComponentName)
                .orElseThrow(() -> new RuntimeException(MessageFormat.format("component.notfound=no component exists with name {0}.", requestComponentName)));
        return transform(request, actionExecution);

    }

    private HttpRequestComponent transform(Component request, ActionExecution actionExecution) {
        if (!request.getType().equalsIgnoreCase("http.request")) {
            throw new RuntimeException(MessageFormat.format("Component ''http.request'' not of type 'http.request' but type {0}", request.getType()));
        }

        DataType uri = request.getParameters().stream()
                .filter(componentParameter -> componentParameter.getName().equalsIgnoreCase("url"))
                .findFirst()
                .map(componentParameter -> httpRequestComponentParameterService.getParameterValue(componentParameter, request.getAttributes(), actionExecution))
                .orElseThrow(() -> new RuntimeException("No url defined in http request"));

        Map<String, DataType> headers = request.getParameters().stream()
                .filter(componentParameter -> componentParameter.getName().startsWith("header"))
                .map(componentParameter -> httpRequestComponentParameterService.getHeader(componentParameter, request.getAttributes(), actionExecution))
                .flatMap(m->m.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Map<String, DataType> queryParameters = request.getParameters().stream()
                .filter(componentParameter -> componentParameter.getName().startsWith("queryparam"))
                .map(componentParameter -> httpRequestComponentParameterService.getQueryParameter(componentParameter, request.getAttributes(), actionExecution))
                .flatMap(m->m.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return new HttpRequestComponent(uri, headers, queryParameters);
    }


    public HttpRequestComponent getHttpRequestComponent(String requestComponentName, Long requestComponentVersion, ActionExecution actionExecution) {
        Component request = ComponentConfiguration.getInstance().get(requestComponentName, requestComponentVersion)
                .orElseThrow(() -> new RuntimeException(MessageFormat.format("component.notfound=no component exists with name {0}.", requestComponentName)));
        return transform(request, actionExecution);
    }

    public ExecutionControl getExecutionControl() {
        return executionControl;
    }

    public void setExecutionControl(ExecutionControl executionControl) {
        this.executionControl = executionControl;
    }

}