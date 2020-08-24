package io.metadew.iesi.component.http;

import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.script.execution.ActionExecution;

public interface IHttpQueryParameterService {

    public HttpQueryParameter convert(String httpQueryParameter, ActionExecution actionExecution);

    public HttpQueryParameter convert(ComponentParameter componentParameter, ActionExecution actionExecution);

    public boolean isQueryParameter(ComponentParameter componentParameter);

}
