package io.metadew.iesi.metadata.service.connection.trace.http;

import io.metadew.iesi.connection.http.HttpConnection;
import io.metadew.iesi.metadata.definition.connection.trace.http.HttpConnectionTrace;
import io.metadew.iesi.script.execution.ActionExecution;

public interface IHttpConnectionTraceService {

    public HttpConnectionTrace convert(HttpConnection httpConnection, ActionExecution actionExecution, String actionParameterName);

    public void trace(HttpConnection httpConnection, ActionExecution actionExecution, String actionParameterName);

}
