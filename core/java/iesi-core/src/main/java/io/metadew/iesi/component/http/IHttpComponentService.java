package io.metadew.iesi.component.http;

import io.metadew.iesi.connection.http.request.HttpRequest;
import io.metadew.iesi.connection.http.request.HttpRequestBuilderException;
import io.metadew.iesi.script.execution.ActionExecution;

import java.net.URISyntaxException;

public interface IHttpComponentService {

    public HttpRequest buildHttpRequest(HttpComponent httpComponent, String body) throws URISyntaxException, HttpRequestBuilderException;

    public HttpRequest buildHttpRequest(HttpComponent httpComponent) throws URISyntaxException, HttpRequestBuilderException;

    public HttpComponent get(String httpComponentReferenceName, ActionExecution actionExecution);

    public String getUri(HttpComponent httpComponent);

    public HttpComponent convert(HttpComponentDefinition httpComponentDefinition, ActionExecution actionExecution);

}
