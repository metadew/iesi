package io.metadew.iesi.component.http;

import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.script.execution.ActionExecution;

public interface IHttpComponentDefinitionService {

    public HttpComponentDefinition convert(Component component, ActionExecution actionExecution, String actionParameterName);

}
