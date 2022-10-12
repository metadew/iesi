package io.metadew.iesi.component.http;

import io.metadew.iesi.metadata.configuration.component.trace.ComponentDesignTraceConfiguration;
import io.metadew.iesi.metadata.definition.component.trace.design.ComponentDesignTraceKey;
import io.metadew.iesi.metadata.definition.component.trace.design.http.*;
import io.metadew.iesi.script.execution.ActionExecution;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class HttpComponentDesignTraceService {

    private static final String COMPONENT_TYPE = "http.request";
    private final ComponentDesignTraceConfiguration componentDesignTraceConfiguration;

    public HttpComponentDesignTraceService(ComponentDesignTraceConfiguration componentDesignTraceConfiguration) {
        this.componentDesignTraceConfiguration = componentDesignTraceConfiguration;
    }


    public HttpComponentDesignTrace convert(HttpComponentDefinition httpComponentDefinition, ActionExecution actionExecution,
                                            String actionParameterName) {
        UUID uuid = UUID.randomUUID();
        return new HttpComponentDesignTrace(
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
                httpComponentDefinition.getHeaders().stream().map(headers -> convertHeaders(headers, uuid)).collect(Collectors.toList()),
                httpComponentDefinition.getQueryParameters().stream().map(queries -> convertQueries(queries, uuid)).collect(Collectors.toList())
        );
    }

    public void trace(HttpComponentDefinition httpComponentDefinition, ActionExecution actionExecution,
                                            String actionParameterName) {
        componentDesignTraceConfiguration.insert(convert(httpComponentDefinition, actionExecution, actionParameterName));
    }

    private HttpComponentHeaderDesignTrace convertHeaders(HttpHeaderDefinition httpHeaderDefinition, UUID id) {
        return new HttpComponentHeaderDesignTrace(
                new HttpComponentHeaderDesignTraceKey(UUID.randomUUID()),
                new ComponentDesignTraceKey(id),
                httpHeaderDefinition.getName(),
                httpHeaderDefinition.getValue()
        );
    }

    private HttpComponentQueryParameterDesignTrace convertQueries(HttpQueryParameterDefinition httpQueryParameterDefinition, UUID id) {
        return new HttpComponentQueryParameterDesignTrace(
                new HttpComponentQueryParameterDesignTraceKey(UUID.randomUUID()),
                new ComponentDesignTraceKey(id),
                httpQueryParameterDefinition.getName(),
                httpQueryParameterDefinition.getValue()
        );
    }
}
