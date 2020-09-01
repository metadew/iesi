package io.metadew.iesi.component.http;

import io.metadew.iesi.metadata.definition.component.ComponentParameter;

public class HttpHeaderDefinitionService implements IHttpHeaderDefinitionService {

    private static HttpHeaderDefinitionService INSTANCE;

    public synchronized static HttpHeaderDefinitionService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HttpHeaderDefinitionService();
        }
        return INSTANCE;
    }

    private HttpHeaderDefinitionService() {
    }


    public HttpHeaderDefinition convert(String httpHeader) {
        return new HttpHeaderDefinition(httpHeader.split(",", 2)[0], httpHeader.split(",", 2)[1]);
    }

    public HttpHeaderDefinition convert(ComponentParameter componentParameter) {
        return convert(componentParameter.getValue());
    }

    public boolean isHeader(ComponentParameter componentParameter) {
        return componentParameter.getMetadataKey().getParameterName().startsWith("header");
    }

}
