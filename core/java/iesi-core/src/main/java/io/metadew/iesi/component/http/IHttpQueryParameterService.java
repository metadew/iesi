package io.metadew.iesi.component.http;

import io.metadew.iesi.script.execution.ActionExecution;

public interface IHttpQueryParameterService {

    public HttpQueryParameter convert(HttpQueryParameterDefinition httpQueryParameterDefinition, ActionExecution actionExecution);

}
