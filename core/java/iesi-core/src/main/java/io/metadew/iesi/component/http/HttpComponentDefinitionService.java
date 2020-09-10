package io.metadew.iesi.component.http;

import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.metadata.definition.component.trace.componentDesign.ComponentDesignTraceConfiguration;
import io.metadew.iesi.script.execution.ActionExecution;

import java.util.stream.Collectors;

public class HttpComponentDefinitionService implements IHttpComponentDefinitionService {

    private static final String COMPONENT_TYPE = "http.request";
    private static final String CONNECTION_REFERENCE_NAME_KEY = "connection";
    private static final String ENDPOINT_KEY = "endpoint";
    private static final String TYPE_KEY = "type";

    private static HttpComponentDefinitionService INSTANCE;

    public synchronized static HttpComponentDefinitionService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HttpComponentDefinitionService();
        }
        return INSTANCE;
    }

    private HttpComponentDefinitionService() {
    }

    public HttpComponentDefinition convert(Component component, ActionExecution actionExecution, String actionParameterName) {
        if (!(component.getType().equalsIgnoreCase(COMPONENT_TYPE))) {
            throw new RuntimeException("Cannot convert " + component.toString() + " to http component");
        }

        HttpComponentDefinition httpComponentDefinition = new HttpComponentDefinition(
                component.getName(),
                component.getVersion().getMetadataKey().getComponentKey().getVersionNumber(),
                component.getDescription(),
                component.getParameters().stream()
                        .filter(componentParameter -> componentParameter.getMetadataKey().getParameterName().equals(CONNECTION_REFERENCE_NAME_KEY))
                        .findFirst()
                        .map(ComponentParameter::getValue)
                        .orElseThrow(() -> new RuntimeException("Http component " + component.toString() + " does not contain a " + CONNECTION_REFERENCE_NAME_KEY)),
                component.getParameters().stream()
                        .filter(componentParameter -> componentParameter.getMetadataKey().getParameterName().equals(ENDPOINT_KEY))
                        .findFirst()
                        .map(ComponentParameter::getValue)
                        .orElseThrow(() -> new RuntimeException("Http component " + component.toString() + " does not contain an " + ENDPOINT_KEY)),
                component.getParameters().stream()
                        .filter(componentParameter -> componentParameter.getMetadataKey().getParameterName().equals(TYPE_KEY))
                        .findFirst()
                        .map(ComponentParameter::getValue)
                        .orElseThrow(() -> new RuntimeException("Http component " + component.toString() + " does not contain a " + TYPE_KEY)),
                component.getParameters().stream()
                        .filter(componentParameter -> HttpHeaderDefinitionService.getInstance().isHeader(componentParameter))
                        .map(ComponentParameter::getValue)
                        .map(componentParameterValue -> HttpHeaderDefinitionService.getInstance().convert(componentParameterValue))
                        .collect(Collectors.toList()),
                component.getParameters().stream()
                        .filter(componentParameter -> HttpQueryParameterDefinitionService.getInstance().isQueryParameter(componentParameter))
                        .map(ComponentParameter::getValue)
                        .map(componentParameterValue -> HttpQueryParameterDefinitionService.getInstance().convert(componentParameterValue))
                        .collect(Collectors.toList())
        );

        ComponentDesignTraceConfiguration.getInstance().insert(HttpComponentDesignTraceService.getInstance().convert(httpComponentDefinition, actionExecution, actionParameterName, COMPONENT_TYPE));

        return httpComponentDefinition;
    }
}
