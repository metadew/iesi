package io.metadew.iesi.component.http;

import io.metadew.iesi.script.execution.ActionExecution;

public interface IHttpHeaderService {

    public HttpHeader convert(HttpHeaderDefinition httpHeader, ActionExecution actionExecution);

}
