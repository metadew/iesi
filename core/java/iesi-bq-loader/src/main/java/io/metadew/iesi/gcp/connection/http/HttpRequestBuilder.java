package io.metadew.iesi.gcp.connection.http;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpRequestBuilder {

    private String type;
    private HttpEntity body;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> queryParameters = new HashMap<>();
    private URI uri;

    public HttpRequestBuilder type(String type) {
        this.type = type;
        return this;
    }

    public HttpRequestBuilder jsonBody(String body) {
        this.body = new StringEntity(body, ContentType.APPLICATION_JSON);
        return this;
    }

    public HttpRequestBuilder textBody(String body) {
        this.body = new StringEntity(body, ContentType.TEXT_PLAIN);
        return this;
    }

    public HttpRequestBuilder body(String body) throws UnsupportedEncodingException {
        this.body = new StringEntity(body);
        return this;
    }

    public HttpRequestBuilder body(String body, ContentType contentType) {
        this.body = new StringEntity(body, contentType);
        return this;
    }

    public HttpRequestBuilder uri(String uri) throws URISyntaxException {
        this.uri = new URIBuilder(uri).build();
        return this;
    }

    public HttpRequestBuilder uri(URI uri) throws URISyntaxException {
        this.uri = uri;
        return this;
    }

    public HttpRequestBuilder headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public HttpRequestBuilder header(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    public HttpRequestBuilder queryParameter(String key, String value) {
        this.queryParameters.put(key, value);
        return this;
    }

    public HttpRequestBuilder queryParameters(Map<String, String> queryParameters) {
        this.queryParameters = queryParameters;
        return this;
    }

    public HttpRequest build() throws HttpRequestBuilderException, URISyntaxException {
        if (type.equalsIgnoreCase("get")) {
            return buildGet();
        } else if (type.equalsIgnoreCase("post")) {
            return buildPost();
        } else {
            throw new HttpRequestBuilderException(MessageFormat.format("Type ''{0}'' not supported", type));
        }
    }

    private HttpRequest buildPost() throws URISyntaxException, HttpRequestBuilderException {
        verifyPostRequestRequirements();

        URIBuilder uriBuilder = new URIBuilder(uri);
        queryParameters.forEach(uriBuilder::addParameter);

        HttpPost httpPost = new HttpPost(uriBuilder.build());

        headers.forEach(httpPost::addHeader);

        getBody().ifPresent(httpPost::setEntity);

        return new HttpPostRequest(httpPost);
    }

    private HttpRequest buildGet() throws URISyntaxException, HttpRequestBuilderException {
        verifyGetRequestRequirements();
        URIBuilder uriBuilder = new URIBuilder(uri);

        queryParameters.forEach(uriBuilder::addParameter);

        HttpGet httpGet = new HttpGet(uriBuilder.build());
        headers.forEach(httpGet::addHeader);

        return new HttpGetRequest(httpGet);
    }

    private void verifyPostRequestRequirements() throws HttpRequestBuilderException {
        if (uri == null) {
            throw new HttpRequestBuilderException("No uri supplied");
        }
    }

    private void verifyGetRequestRequirements() throws HttpRequestBuilderException {
        if (uri == null) {
            throw new HttpRequestBuilderException("No uri supplied");
        }
    }

    private Optional<HttpEntity> getBody() {
        return Optional.ofNullable(body);
    }

}
