package io.metadew.iesi.component.http;

import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.connection.http.HttpConnectionService;
import io.metadew.iesi.connection.http.request.HttpRequest;
import io.metadew.iesi.connection.http.request.HttpRequestBuilder;
import io.metadew.iesi.connection.http.request.HttpRequestBuilderException;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.configuration.action.design.ActionParameterDesignTraceConfiguration;
import io.metadew.iesi.metadata.configuration.component.ComponentConfiguration;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.action.design.ActionParameterDesignTrace;
import io.metadew.iesi.metadata.definition.action.key.ActionParameterKey;
import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.service.action.ActionParameterTraceService;
import io.metadew.iesi.metadata.service.connection.trace.http.HttpConnectionTraceService;
import io.metadew.iesi.script.action.http.HttpExecuteRequest;
import io.metadew.iesi.script.execution.ActionExecution;
import lombok.extern.log4j.Log4j2;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.stream.Collectors;

@Service
@Log4j2
public class HttpComponentService implements IHttpComponentService {

    private final FrameworkCrypto frameworkCrypto;
    private ComponentConfiguration componentConfiguration;

    private ActionParameterDesignTraceConfiguration actionParameterDesignTraceConfiguration;
    private ActionParameterTraceService actionParameterTraceService;
    private HttpConnectionService httpConnectionService;
    private HttpComponentTraceService httpComponentTraceService;
    private HttpConnectionTraceService httpConnectionTraceService;
    private HttpComponentDefinitionService httpComponentDefinitionService;
    private HttpQueryParameterService httpQueryParameterService;
    private DataTypeHandler dataTypeHandler;
    private HttpHeaderService httpHeaderService;

    public HttpComponentService(FrameworkCrypto frameworkCrypto) {
        this.frameworkCrypto = frameworkCrypto;
    }

    public HttpRequest buildHttpRequest(HttpComponent httpComponent, String body) throws URISyntaxException, HttpRequestBuilderException {
        HttpRequestBuilder httpRequestBuilder = new HttpRequestBuilder()
                .type(httpComponent.getType())
                .uri(getUri(httpComponent))
                .headers(httpComponent.getHeaders().stream().collect(Collectors.toMap(HttpHeader::getName, HttpHeader::getValue)))
                .queryParameters(httpComponent.getQueryParameters().stream().collect(Collectors.toMap(HttpQueryParameter::getName, HttpQueryParameter::getValue)))
                .body(body, ContentType.getByMimeType(
                                httpComponent.getHeaders().stream()
                                        .filter(httpHeader -> httpHeader.getName().equalsIgnoreCase("Content-Type"))
                                        .findFirst()
                                        .map(HttpHeader::getValue)
                                        .orElse("text/plain")
                        )
                );
        return httpRequestBuilder.build();
    }


    public HttpRequest buildHttpRequest(HttpComponent httpComponent) throws URISyntaxException, HttpRequestBuilderException {
        HttpRequestBuilder httpRequestBuilder = new HttpRequestBuilder()
                .type(httpComponent.getType())
                .uri(getUri(httpComponent))
                .headers(httpComponent.getHeaders().stream().collect(Collectors.toMap(HttpHeader::getName, HttpHeader::getValue)))
                .queryParameters(httpComponent.getQueryParameters().stream().collect(Collectors.toMap(HttpQueryParameter::getName, HttpQueryParameter::getValue)));
        return httpRequestBuilder.build();
    }

    @Override
    public HttpComponent get(String httpComponentReferenceName, ActionExecution actionExecution) {
        Component component = componentConfiguration.getByNameAndVersion(httpComponentReferenceName, 1L)
                .orElseThrow(() -> new RuntimeException("Could not find http component with name " + httpComponentReferenceName + "and version 1"));
        HttpComponentDefinition httpComponentDefinition = httpComponentDefinitionService.convert(component, actionExecution);
        return convert(httpComponentDefinition, actionExecution);
    }

    @Override
    public HttpComponent getAndTrace(String httpComponentReferenceName, ActionExecution actionExecution, String actionParameterName, Long componentVersion) {
        Component component = componentConfiguration.getByNameAndVersion(httpComponentReferenceName, componentVersion)
                .orElseThrow(() -> new RuntimeException("Could not find http component with name " + httpComponentReferenceName + " and version " + componentVersion));
        HttpComponentDefinition httpComponentDefinition = httpComponentDefinitionService.convertAndTrace(component, actionExecution, actionParameterName);
        return convertAndTrace(httpComponentDefinition, actionExecution, actionParameterName);
    }

    @Override
    public HttpComponent getAndTrace(String httpComponentReferenceName, ActionExecution actionExecution, String actionParameterName, String componentVersionParameterName) {
        Component component = componentConfiguration.getByNameAndLatestVersion(httpComponentReferenceName)
                .orElseThrow(() -> new RuntimeException("Could not find http component with name " + httpComponentReferenceName));
        HttpComponentDefinition httpComponentDefinition = httpComponentDefinitionService.convertAndTrace(component, actionExecution, actionParameterName);
        traceEmptyVersion(actionExecution, componentVersionParameterName, httpComponentDefinition.getVersion());
        return convertAndTrace(httpComponentDefinition, actionExecution, actionParameterName);
    }

    @Override
    public String getUri(HttpComponent httpComponent) {
        return httpConnectionService.getBaseUri(httpComponent.getHttpConnection()) +
                httpComponent.getEndpoint();
    }

    @Override
    public HttpComponent convert(HttpComponentDefinition httpComponentDefinition, ActionExecution actionExecution) {
        return new HttpComponent(
                httpComponentDefinition.getReferenceName(),
                httpComponentDefinition.getVersion(),
                httpComponentDefinition.getDescription(),
                httpConnectionService.get(httpComponentDefinition.getHttpConnectionReferenceName(), actionExecution),
                resolveEndpoint(httpComponentDefinition.getEndpoint(), actionExecution),
                resolveType(httpComponentDefinition.getType(), actionExecution),
                httpComponentDefinition.getHeaders().stream()
                        .map(header -> httpHeaderService.convert(header, actionExecution))
                        .collect(Collectors.toList()),
                httpComponentDefinition.getQueryParameters().stream()
                        .map(queryParameter -> httpQueryParameterService.convert(queryParameter, actionExecution))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public HttpComponent convertAndTrace(HttpComponentDefinition httpComponentDefinition, ActionExecution actionExecution, String actionParameterName) {
        HttpComponent httpComponent = convert(httpComponentDefinition, actionExecution);
        httpComponentTraceService.trace(httpComponent, actionExecution, actionParameterName);
        httpConnectionTraceService.trace(httpComponent.getHttpConnection(), actionExecution, actionParameterName);
        return httpComponent;
    }

    protected String resolveEndpoint(String endpoint, ActionExecution actionExecution) {
        String actionResolvedValue = actionExecution.getActionControl().getActionRuntime().resolveRuntimeVariables(endpoint);
        String resolvedInputValue = actionExecution.getExecutionControl().getExecutionRuntime().resolveVariables(actionExecution, actionResolvedValue);
        resolvedInputValue = actionExecution.getExecutionControl().getExecutionRuntime().resolveConceptLookup(resolvedInputValue).getValue();
        resolvedInputValue = actionExecution.getExecutionControl().getExecutionRuntime().resolveVariables(actionExecution, resolvedInputValue);
        String decryptedInputValue = frameworkCrypto.resolve(resolvedInputValue);
        return convertEndpointDatatype(dataTypeHandler.resolve(decryptedInputValue, actionExecution.getExecutionControl().getExecutionRuntime()));
    }

    protected String resolveType(String type, ActionExecution actionExecution) {
        String actionResolvedValue = actionExecution.getActionControl().getActionRuntime().resolveRuntimeVariables(type);
        String resolvedInputValue = actionExecution.getExecutionControl().getExecutionRuntime().resolveVariables(actionExecution, actionResolvedValue);
        resolvedInputValue = actionExecution.getExecutionControl().getExecutionRuntime().resolveConceptLookup(resolvedInputValue).getValue();
        resolvedInputValue = actionExecution.getExecutionControl().getExecutionRuntime().resolveVariables(actionExecution, resolvedInputValue);
        String decryptedInputValue = frameworkCrypto.resolve(resolvedInputValue);
        return convertTypeDatatype(dataTypeHandler.resolve(decryptedInputValue, actionExecution.getExecutionControl().getExecutionRuntime()));
    }

    private String convertTypeDatatype(DataType type) {
        if (type instanceof Text) {
            return ((Text) type).getString();
        } else {
            throw new RuntimeException(MessageFormat.format("Output http component does not allow type to be of ''{0}''", type.getClass().getSimpleName()));
        }
    }

    private String convertEndpointDatatype(DataType endpoint) {
        if (endpoint instanceof Text) {
            return ((Text) endpoint).getString();
        } else {
            throw new RuntimeException(MessageFormat.format("Output http component does not allow endpoint to be of ''{0}''", endpoint.getClass().getSimpleName()));
        }
    }

    public void traceEmptyVersion(ActionExecution actionExecution, String actionParameterName, Long version) {
        ActionParameter actionParameter = new ActionParameter(
                new ActionParameterKey(
                        actionExecution.getScriptExecution().getScript().getMetadataKey().getScriptId(),
                        actionExecution.getScriptExecution().getScript().getMetadataKey().getScriptVersion(),
                        actionExecution.getAction().getMetadataKey().getActionId(),
                        actionParameterName
                ),
                ""
        );

        ActionParameterDesignTrace actionParameterDesignTrace = new ActionParameterDesignTrace(
                actionExecution.getExecutionControl().getRunId(),
                actionExecution.getProcessId(),
                actionExecution.getAction().getMetadataKey().getActionId(),
                actionParameter
        );

        if (!actionParameterDesignTraceConfiguration.get(actionParameterDesignTrace.getMetadataKey()).isPresent()) {
            actionParameterDesignTraceConfiguration.insert(actionParameterDesignTrace);
            actionParameterTraceService.trace(
                    actionExecution,
                    actionParameter.getMetadataKey().getParameterName(),
                    new Text(version.toString())
            );
        } else {
            ((HttpExecuteRequest) actionExecution.getActionTypeExecution())
                    .replaceParameterResolvedValue(actionParameter, version.toString());
        }
    }

    @Autowired(required = false)
    public void setComponentConfiguration(ComponentConfiguration componentConfiguration) {
        this.componentConfiguration = componentConfiguration;
    }
    @Autowired(required = false)
    public void setActionParameterDesignTraceConfiguration(ActionParameterDesignTraceConfiguration actionParameterDesignTraceConfiguration) {
        this.actionParameterDesignTraceConfiguration = actionParameterDesignTraceConfiguration;
    }
    @Autowired(required = false)
    public void setActionParameterTraceService(ActionParameterTraceService actionParameterTraceService) {
        this.actionParameterTraceService = actionParameterTraceService;
    }
    @Autowired(required = false)
    public void setHttpConnectionService(HttpConnectionService httpConnectionService) {
        this.httpConnectionService = httpConnectionService;
    }
    @Autowired(required = false)
    public void setHttpComponentTraceService(HttpComponentTraceService httpComponentTraceService) {
        this.httpComponentTraceService = httpComponentTraceService;
    }
    @Autowired(required = false)
    public void setHttpConnectionTraceService(HttpConnectionTraceService httpConnectionTraceService) {
        this.httpConnectionTraceService = httpConnectionTraceService;
    }
    @Autowired(required = false)
    public void setHttpComponentDefinitionService(HttpComponentDefinitionService httpComponentDefinitionService) {
        this.httpComponentDefinitionService = httpComponentDefinitionService;
    }
    @Autowired(required = false)
    public void setHttpQueryParameterService(HttpQueryParameterService httpQueryParameterService) {
        this.httpQueryParameterService = httpQueryParameterService;
    }
    @Autowired(required = false)
    public void setDataTypeHandler(DataTypeHandler dataTypeHandler) {
        this.dataTypeHandler = dataTypeHandler;
    }
    @Autowired(required = false)
    public void setHttpHeaderService(HttpHeaderService httpHeaderService) {
        this.httpHeaderService = httpHeaderService;
    }
}
