package io.metadew.iesi.component.http;

import io.metadew.iesi.metadata.definition.component.trace.componentTrace.*;
import io.metadew.iesi.script.execution.ActionExecution;

import java.util.UUID;
import java.util.stream.Collectors;

public class HttpComponentTraceService {

    public HttpComponentTrace convert(HttpComponent httpComponent,
                                      ActionExecution actionExecution, String actionParameterName, String component_type) {
        UUID uuid = UUID.randomUUID();
        return new HttpComponentTrace(
                new ComponentTraceKey(uuid),
                actionExecution.getExecutionControl().getRunId(),
                actionExecution.getExecutionControl().getProcessId(),
                actionParameterName,
                component_type,
                httpComponent.getReferenceName(),
                httpComponent.getDescription(),
                httpComponent.getVersion(),
                httpComponent.getHttpConnection().getReferenceName(),
                httpComponent.getType(),
                httpComponent.getEndpoint(),
                httpComponent.getHeaders().stream().map(header -> convertHeaders(header, uuid))
                        .collect(Collectors.toList()),
                httpComponent.getQueryParameters().stream().map(queries -> convertQueries(queries, uuid))
                        .collect(Collectors.toList())
        );

    }

    private HttpComponentHeaderTrace convertHeaders(HttpHeader httpHeader, UUID id) {
        return new HttpComponentHeaderTrace(
                new HttpComponentHeaderTraceKey(UUID.randomUUID()),
                new ComponentTraceKey(id),
                httpHeader.getName(),
                httpHeader.getValue()
        );
    }

    private HttpComponentQueryTrace convertQueries(HttpQueryParameter httpQueryParameter, UUID id) {
        return new HttpComponentQueryTrace(
                new HttpComponentQueryTraceKey(UUID.randomUUID()),
                new ComponentTraceKey(id),
                httpQueryParameter.getName(),
                httpQueryParameter.getValue()
        );
    }
}
