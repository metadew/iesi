package io.metadew.iesi.component.http;

import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.connection.http.HttpConnectionService;
import io.metadew.iesi.connection.http.request.HttpRequest;
import io.metadew.iesi.connection.http.request.HttpRequestBuilder;
import io.metadew.iesi.connection.http.request.HttpRequestBuilderException;
import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.configuration.component.ComponentConfiguration;
import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.service.connection.trace.http.HttpConnectionTraceService;
import io.metadew.iesi.script.execution.ActionExecution;
import lombok.extern.log4j.Log4j2;
import org.apache.http.entity.ContentType;

import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.stream.Collectors;

@Log4j2
public class HttpComponentService implements IHttpComponentService {

    private static HttpComponentService instance;

    public static synchronized HttpComponentService getInstance() {
        if (instance == null) {
            instance = new HttpComponentService();
        }
        return instance;
    }

    private HttpComponentService() {
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
        Component component = ComponentConfiguration.getInstance().getByNameAndVersion(httpComponentReferenceName, 1L)
                .orElseThrow(() -> new RuntimeException("Could not find http component with name " + httpComponentReferenceName + "and version 1"));
        HttpComponentDefinition httpComponentDefinition = HttpComponentDefinitionService.getInstance().convert(component, actionExecution);
        return convert(httpComponentDefinition, actionExecution);
    }

    @Override
    public HttpComponent getAndTrace(String httpComponentReferenceName, ActionExecution actionExecution, String actionParameterName, Long version) {
        Component component = ComponentConfiguration.getInstance().getByNameAndVersion(httpComponentReferenceName, version)
                .orElseThrow(() -> new RuntimeException("Could not find http component with name " + httpComponentReferenceName + " and version " + version));
        HttpComponentDefinition httpComponentDefinition = HttpComponentDefinitionService.getInstance().convertAndTrace(component, actionExecution, actionParameterName);
        return convertAndTrace(httpComponentDefinition, actionExecution, actionParameterName);
    }

    @Override
    public String getUri(HttpComponent httpComponent) {
        return HttpConnectionService.getInstance().getBaseUri(httpComponent.getHttpConnection()) +
                httpComponent.getEndpoint();
    }

    @Override
    public HttpComponent convert(HttpComponentDefinition httpComponentDefinition, ActionExecution actionExecution) {
        return new HttpComponent(
                httpComponentDefinition.getReferenceName(),
                httpComponentDefinition.getVersion(),
                httpComponentDefinition.getDescription(),
                HttpConnectionService.getInstance().get(httpComponentDefinition.getHttpConnectionReferenceName(), actionExecution),
                resolveEndpoint(httpComponentDefinition.getEndpoint(), actionExecution),
                resolveType(httpComponentDefinition.getType(), actionExecution),
                httpComponentDefinition.getHeaders().stream()
                        .map(header -> HttpHeaderService.getInstance().convert(header, actionExecution))
                        .collect(Collectors.toList()),
                httpComponentDefinition.getQueryParameters().stream()
                        .map(queryParameter -> HttpQueryParameterService.getInstance().convert(queryParameter, actionExecution))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public HttpComponent convertAndTrace(HttpComponentDefinition httpComponentDefinition, ActionExecution actionExecution, String actionParameterName) {
        HttpComponent httpComponent = convert(httpComponentDefinition, actionExecution);
        HttpComponentTraceService.getInstance().trace(httpComponent, actionExecution, actionParameterName);
        HttpConnectionTraceService.getInstance().trace(httpComponent.getHttpConnection(), actionExecution, actionParameterName);
        return httpComponent;
    }

    protected String resolveEndpoint(String endpoint, ActionExecution actionExecution) {
        String actionResolvedValue = actionExecution.getActionControl().getActionRuntime().resolveRuntimeVariables(endpoint);
        String resolvedInputValue = actionExecution.getExecutionControl().getExecutionRuntime().resolveVariables(actionExecution, actionResolvedValue);
        resolvedInputValue = actionExecution.getExecutionControl().getExecutionRuntime().resolveConceptLookup(resolvedInputValue).getValue();
        resolvedInputValue = actionExecution.getExecutionControl().getExecutionRuntime().resolveVariables(actionExecution, resolvedInputValue);
        String decryptedInputValue = FrameworkCrypto.getInstance().resolve(resolvedInputValue);
        return convertEndpointDatatype(DataTypeHandler.getInstance().resolve(decryptedInputValue, actionExecution.getExecutionControl().getExecutionRuntime()));
    }

    protected String resolveType(String type, ActionExecution actionExecution) {
        String actionResolvedValue = actionExecution.getActionControl().getActionRuntime().resolveRuntimeVariables(type);
        String resolvedInputValue = actionExecution.getExecutionControl().getExecutionRuntime().resolveVariables(actionExecution, actionResolvedValue);
        resolvedInputValue = actionExecution.getExecutionControl().getExecutionRuntime().resolveConceptLookup(resolvedInputValue).getValue();
        resolvedInputValue = actionExecution.getExecutionControl().getExecutionRuntime().resolveVariables(actionExecution, resolvedInputValue);
        String decryptedInputValue = FrameworkCrypto.getInstance().resolve(resolvedInputValue);
        return convertTypeDatatype(DataTypeHandler.getInstance().resolve(decryptedInputValue, actionExecution.getExecutionControl().getExecutionRuntime()));
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

}
