package io.metadew.iesi.connection.http;

import io.metadew.iesi.metadata.definition.connection.Connection;

public interface IHttpConnectionDefinitionService {

    public HttpConnectionDefinition convert(Connection connection);

}
