package io.metadew.iesi.component.http;

import io.metadew.iesi.metadata.definition.component.ComponentParameter;

public class HttpQueryParameterDefinitionService implements IHttpQueryParameterDefinitionService {


    private static HttpQueryParameterDefinitionService INSTANCE;

    public synchronized static HttpQueryParameterDefinitionService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HttpQueryParameterDefinitionService();
        }
        return INSTANCE;
    }

    private HttpQueryParameterDefinitionService() {
    }


    public HttpQueryParameterDefinition convert(String httpQueryParameter) {
        return new HttpQueryParameterDefinition(httpQueryParameter.split(",", 2)[0], httpQueryParameter.split(",", 2)[1]);
    }

    public HttpQueryParameterDefinition convert(ComponentParameter componentParameter) {
        return convert(componentParameter.getValue());
    }

    public boolean isQueryParameter(ComponentParameter componentParameter) {
        return componentParameter.getMetadataKey().getParameterName().startsWith("queryparam");
    }

}
