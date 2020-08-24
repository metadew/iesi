package io.metadew.iesi.component.http;

import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.script.execution.ActionExecution;

public interface IHttpHeaderService {

    public HttpHeader convert(String httpHeader, ActionExecution actionExecution);

    public HttpHeader convert(ComponentParameter componentParameter, ActionExecution actionExecution);

    public boolean isHeader(ComponentParameter componentParameter);

}
