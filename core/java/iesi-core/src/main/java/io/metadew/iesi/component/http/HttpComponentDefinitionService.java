package io.metadew.iesi.component.http;

import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.script.execution.ActionExecution;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class HttpComponentDefinitionService implements IHttpComponentDefinitionService {

    private static final String COMPONENT_TYPE = "http.request";
    private static final String CONNECTION_REFERENCE_NAME_KEY = "connection";
    private static final String ENDPOINT_KEY = "endpoint";
    private static final String TYPE_KEY = "type";

    private final HttpComponentDesignTraceService httpComponentDesignTraceService;

    public HttpComponentDefinitionService(HttpComponentDesignTraceService httpComponentDesignTraceService) {
        this.httpComponentDesignTraceService = httpComponentDesignTraceService;
    }

    public HttpComponentDefinition convert(Component component, ActionExecution actionExecution) {
        if (!(component.getType().equalsIgnoreCase(COMPONENT_TYPE))) {
            throw new RuntimeException("Cannot convert " + component.toString() + " to http component");
        }
        return new HttpComponentDefinition(
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
    }

    @Override
    public HttpComponentDefinition convertAndTrace(Component component, ActionExecution actionExecution, String actionParameterName) {
        HttpComponentDefinition httpComponentDefinition = convert(component, actionExecution);
        httpComponentDesignTraceService.trace(httpComponentDefinition, actionExecution, actionParameterName);
        return httpComponentDefinition;
    }
}
