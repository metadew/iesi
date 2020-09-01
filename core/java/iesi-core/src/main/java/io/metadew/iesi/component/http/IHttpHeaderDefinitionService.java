package io.metadew.iesi.component.http;

import io.metadew.iesi.metadata.definition.component.ComponentParameter;

public interface IHttpHeaderDefinitionService {

    public HttpHeaderDefinition convert(String httpHeader);

    public HttpHeaderDefinition convert(ComponentParameter componentParameter);

    public boolean isHeader(ComponentParameter componentParameter);

}
