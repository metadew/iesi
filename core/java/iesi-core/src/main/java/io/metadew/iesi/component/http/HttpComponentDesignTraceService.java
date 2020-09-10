package io.metadew.iesi.component.http;

import io.metadew.iesi.metadata.definition.component.trace.componentDesign.*;
import io.metadew.iesi.script.execution.ActionExecution;

import java.util.UUID;
import java.util.stream.Collectors;

public class HttpComponentDesignTraceService {

    private static HttpComponentDesignTraceService INSTANCE;

    public synchronized static HttpComponentDesignTraceService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HttpComponentDesignTraceService();
        }
        return INSTANCE;
    }

    public HttpComponentDesignTrace convert(HttpComponentDefinition httpComponentDefinition, ActionExecution actionExecution,
                                            String actionParameterName, String component_type) {
        UUID uuid = UUID.randomUUID();
        return new HttpComponentDesignTrace(
                new ComponentDesignTraceKey(uuid),
                actionExecution.getExecutionControl().getRunId(),
                actionExecution.getExecutionControl().getProcessId(),
                actionParameterName,
                component_type,
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
