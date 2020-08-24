package io.metadew.iesi.connection.http;

public interface IHttpConnectionService {

    public HttpConnection get(String httpConnectionReferenceName, String environmentReferenceName);

    public String getBaseUri(HttpConnection httpConnection);

    public HttpConnection convert(HttpConnectionDefinition httpConnectionDefinition);

}
