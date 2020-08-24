package io.metadew.iesi.connection.http;

import io.metadew.iesi.script.execution.ActionExecution;

public interface IHttpConnectionService {

    public HttpConnection get(String httpConnectionReferenceName, ActionExecution actionExecution);

    public String getBaseUri(HttpConnection httpConnection);

    public HttpConnection convert(HttpConnectionDefinition httpConnectionDefinition);

}
