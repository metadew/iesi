package io.metadew.iesi.connection.http;

import io.metadew.iesi.script.execution.ActionExecution;

public interface IHttpConnectionService {

    HttpConnection get(String httpConnectionReferenceName,  ActionExecution actionExecution);

    HttpConnection getAndTrace(String httpConnectionReferenceName, ActionExecution actionExecution, String actionParameterName);

    String getBaseUri(HttpConnection httpConnection);

    HttpConnection convert(HttpConnectionDefinition httpConnectionDefinition);

}
