package io.metadew.iesi.component.http;

import io.metadew.iesi.metadata.configuration.component.trace.ComponentTraceConfiguration;
import io.metadew.iesi.metadata.definition.component.trace.ComponentTraceKey;
import io.metadew.iesi.metadata.definition.component.trace.http.*;
import io.metadew.iesi.script.execution.ActionExecution;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class HttpComponentTraceService {

    private static final String COMPONENT_TYPE = "http.request";

    private final ComponentTraceConfiguration componentTraceConfiguration;

    public HttpComponentTraceService(ComponentTraceConfiguration componentTraceConfiguration) {
        this.componentTraceConfiguration = componentTraceConfiguration;
    }

    public HttpComponentTrace convert(HttpComponent httpComponent,
                                      ActionExecution actionExecution, String actionParameterName) {
        UUID uuid = UUID.randomUUID();
        return new HttpComponentTrace(
                new ComponentTraceKey(uuid),
                actionExecution.getExecutionControl().getRunId(),
                actionExecution.getExecutionControl().getProcessId(),
                actionParameterName,
                COMPONENT_TYPE,
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

    public void trace(HttpComponent httpComponent, ActionExecution actionExecution, String actionParameterName) {
        componentTraceConfiguration.insert(convert(httpComponent, actionExecution, actionParameterName));
    }

    private HttpComponentHeaderTrace convertHeaders(HttpHeader httpHeader, UUID id) {
        return new HttpComponentHeaderTrace(
                new HttpComponentHeaderTraceKey(UUID.randomUUID()),
                new ComponentTraceKey(id),
                httpHeader.getName(),
                httpHeader.getValue()
        );
    }

    private HttpComponentQueryParameterTrace convertQueries(HttpQueryParameter httpQueryParameter, UUID id) {
        return new HttpComponentQueryParameterTrace(
                new HttpComponentQueryParameterTraceKey(UUID.randomUUID()),
                new ComponentTraceKey(id),
                httpQueryParameter.getName(),
                httpQueryParameter.getValue()
        );
    }
}
