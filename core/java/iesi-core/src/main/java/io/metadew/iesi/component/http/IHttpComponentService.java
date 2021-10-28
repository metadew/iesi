package io.metadew.iesi.component.http;

import io.metadew.iesi.connection.http.request.HttpRequest;
import io.metadew.iesi.connection.http.request.HttpRequestBuilderException;
import io.metadew.iesi.script.execution.ActionExecution;

import java.net.URISyntaxException;

public interface IHttpComponentService {

    HttpRequest buildHttpRequest(HttpComponent httpComponent, String body) throws URISyntaxException, HttpRequestBuilderException;

    HttpRequest buildHttpRequest(HttpComponent httpComponent) throws URISyntaxException, HttpRequestBuilderException;

    HttpComponent get(String httpComponentReferenceName, ActionExecution actionExecution);

    HttpComponent getAndTrace(String httpComponentReferenceName, ActionExecution actionExecution, String actionParameterName);

    HttpComponent getAndTrace(String httpComponentReferenceName, ActionExecution actionExecution, String actionParameterName, Long version);

    String getUri(HttpComponent httpComponent);

    HttpComponent convert(HttpComponentDefinition httpComponentDefinition, ActionExecution actionExecution);

    HttpComponent convertAndTrace(HttpComponentDefinition httpComponentDefinition, ActionExecution actionExecution, String actionParameterName);

}
