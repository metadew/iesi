package io.metadew.iesi.metadata.service;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.metadata.configuration.component.ComponentConfiguration;
import io.metadew.iesi.metadata.configuration.script.design.ScriptVersionDesignTraceConfiguration;
import io.metadew.iesi.metadata.definition.HttpRequestComponent;
import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.tools.IdentifierTools;
import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;

import java.text.MessageFormat;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestComponentService {

    private static HttpRequestComponentService INSTANCE;

    public synchronized static HttpRequestComponentService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HttpRequestComponentService();
        }
        return INSTANCE;
    }

    private HttpRequestComponentService() {
    }

    public HttpRequestComponent getHttpRequestComponent(String requestComponentName, ActionExecution actionExecution, ExecutionControl executionControl) {
        Component request = ComponentConfiguration.getInstance().get(IdentifierTools.getComponentIdentifier(requestComponentName))
                .orElseThrow(() -> new RuntimeException(MessageFormat.format("component.notfound=no component exists with name {0}.", requestComponentName)));
        return transform(request, actionExecution, executionControl);

    }

    private HttpRequestComponent transform(Component request, ActionExecution actionExecution, ExecutionControl executionControl) {
        if (!request.getType().equalsIgnoreCase("http.request")) {
            throw new RuntimeException(MessageFormat.format("Component ''http.request'' not of type 'http.request' but type {0}", request.getType()));
        }

        DataType uri = request.getParameters().stream()
                .filter(componentParameter -> componentParameter.getMetadataKey().getParameterName().equalsIgnoreCase("url"))
                .findFirst()
                .map(componentParameter -> {
                    try {
                        return HttpRequestComponentParameterService.getInstance().getParameterValue(componentParameter, request.getAttributes(), actionExecution, executionControl);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .orElseThrow(() -> new RuntimeException("No url defined in http request"));

        Map<String, DataType> headers = request.getParameters().stream()
                .filter(componentParameter -> componentParameter.getMetadataKey().getParameterName().startsWith("header"))
                .map(componentParameter -> {
                    try {
                        return HttpRequestComponentParameterService.getInstance().getHeader(componentParameter, request.getAttributes(), actionExecution, executionControl);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .flatMap(m->m.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Map<String, DataType> queryParameters = request.getParameters().stream()
                .filter(componentParameter -> componentParameter.getMetadataKey().getParameterName().startsWith("queryparam"))
                .map(componentParameter -> {
                    try {
                        return HttpRequestComponentParameterService.getInstance().getQueryParameter(componentParameter, request.getAttributes(), actionExecution, executionControl);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .flatMap(m->m.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return new HttpRequestComponent(uri, headers, queryParameters);
    }


    public HttpRequestComponent getHttpRequestComponent(String requestComponentName, Long requestComponentVersion, ActionExecution actionExecution, ExecutionControl executionControl) {
        Component request = ComponentConfiguration.getInstance().get(new ComponentKey(requestComponentName, requestComponentVersion))
                .orElseThrow(() -> new RuntimeException(MessageFormat.format("component.notfound=no component exists with name {0}.", requestComponentName)));
        return transform(request, actionExecution, executionControl);
    }

}