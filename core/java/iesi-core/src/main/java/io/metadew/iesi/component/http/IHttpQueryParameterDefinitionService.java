package io.metadew.iesi.component.http;

import io.metadew.iesi.metadata.definition.component.ComponentParameter;

public interface IHttpQueryParameterDefinitionService {

    public HttpQueryParameterDefinition convert(String httpQueryParameter);

    public HttpQueryParameterDefinition convert(ComponentParameter componentParameter);

    public boolean isQueryParameter(ComponentParameter componentParameter);

}
