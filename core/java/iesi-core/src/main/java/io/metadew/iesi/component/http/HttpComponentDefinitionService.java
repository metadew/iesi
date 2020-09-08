package io.metadew.iesi.component.http;

import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.metadata.definition.component.trace.componentDesign.*;
import io.metadew.iesi.script.execution.ActionExecution;

import java.util.UUID;
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

    private HttpComponentDefinitionService() { }

    public HttpComponentHeaderDesign convertHeaders(HttpHeaderDefinition httpHeaderDefinition, UUID id) {
        UUID uuid = UUID.randomUUID();
        return new HttpComponentHeaderDesign(
                uuid,
                new HttpComponentDesignTraceKey(id),
                httpHeaderDefinition.getName(),
                httpHeaderDefinition.getValue()
        );
    }

    public HttpComponentQueryDesign convertQueries(HttpQueryParameterDefinition httpQueryParameterDefinition, UUID id) {
        UUID uuid = UUID.randomUUID();
        return new HttpComponentQueryDesign(
                uuid,
                new HttpComponentQueryDesignKey(id),
                httpQueryParameterDefinition.getName(),
                httpQueryParameterDefinition.getValue()
        );
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
        UUID uuid = UUID.randomUUID();
        HttpComponentDesignTrace httpComponentDesignTrace = new HttpComponentDesignTrace(
               new ComponentDesignTraceKey(uuid),
                actionExecution.getExecutionControl().getRunId(),
                actionExecution.getExecutionControl().getProcessId(),
                actionParameterName,
                COMPONENT_TYPE,
                httpComponentDefinition.getReferenceName(),
                httpComponentDefinition.getDescription(),
                httpComponentDefinition.getVersion(),
                httpComponentDefinition.getHttpConnectionReferenceName(),
                httpComponentDefinition.getType(),
                httpComponentDefinition.getEndpoint(),
                httpComponentDefinition.getHeaders().stream().map(headers -> convertHeaders(headers,uuid)).collect(Collectors.toList()),
                httpComponentDefinition.getQueryParameters().stream().map(queries -> convertQueries(queries,uuid)).collect(Collectors.toList())
        );
        ComponentDesignTraceConfiguration.getInstance().insert(httpComponentDesignTrace);

        return httpComponentDefinition;
    }
}
