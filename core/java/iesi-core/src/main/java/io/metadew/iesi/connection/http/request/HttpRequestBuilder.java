package io.metadew.iesi.connection.http.request;

import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
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

@Log4j2
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
        } else if (type.equalsIgnoreCase("put")) {
            return buildPut();
        } else if (type.equalsIgnoreCase("delete")) {
            return buildDelete();
        } else if (type.equalsIgnoreCase("head")) {
            return buildHead();
        } else if (type.equalsIgnoreCase("options")) {
            return buildOptions();
        } else if (type.equalsIgnoreCase("patch")) {
            return buildPatch();
        } else if (type.equalsIgnoreCase("trace")) {
            return buildTrace();
        } else {
            throw new HttpRequestBuilderException(MessageFormat.format("Type ''{0}'' not supported", type));
        }
    }

    private HttpTraceRequest buildTrace() throws HttpRequestBuilderException, URISyntaxException {
        verifyRequestRequirements();
        URIBuilder uriBuilder = new URIBuilder(uri);
        addQueryParameters(uriBuilder);
        HttpTrace httpTrace = new HttpTrace(uriBuilder.build());
        addHeaders(httpTrace);
        return new HttpTraceRequest(httpTrace);
    }

    private HttpPatchRequest buildPatch() throws HttpRequestBuilderException, URISyntaxException {
        verifyRequestRequirements();
        URIBuilder uriBuilder = new URIBuilder(uri);
        addQueryParameters(uriBuilder);
        HttpPatch httpPatch = new HttpPatch(uriBuilder.build());
        addHeaders(httpPatch);
        addBody(httpPatch);
        return new HttpPatchRequest(httpPatch);
    }

    private HttpOptionsRequest buildOptions() throws HttpRequestBuilderException, URISyntaxException {
        verifyRequestRequirements();
        URIBuilder uriBuilder = new URIBuilder(uri);
        addQueryParameters(uriBuilder);
        HttpOptions httpOptions = new HttpOptions(uriBuilder.build());
        addHeaders(httpOptions);
        return new HttpOptionsRequest(httpOptions);
    }

    private HttpHeadRequest buildHead() throws HttpRequestBuilderException, URISyntaxException {
        verifyRequestRequirements();
        URIBuilder uriBuilder = new URIBuilder(uri);
        addQueryParameters(uriBuilder);
        HttpHead httpHead = new HttpHead(uriBuilder.build());
        addHeaders(httpHead);
        return new HttpHeadRequest(httpHead);
    }

    private HttpDeleteRequest buildDelete() throws HttpRequestBuilderException, URISyntaxException {
        verifyRequestRequirements();
        URIBuilder uriBuilder = new URIBuilder(uri);
        addQueryParameters(uriBuilder);
        HttpDelete httpDelete = new HttpDelete(uriBuilder.build());
        addHeaders(httpDelete);
        return new HttpDeleteRequest(httpDelete);
    }

    private HttpPutRequest buildPut() throws HttpRequestBuilderException, URISyntaxException {
        verifyRequestRequirements();
        URIBuilder uriBuilder = new URIBuilder(uri);
        addQueryParameters(uriBuilder);
        HttpPut httpPut = new HttpPut(uriBuilder.build());
        addHeaders(httpPut);
        addBody(httpPut);
        return new HttpPutRequest(httpPut);
    }

    private HttpPostRequest buildPost() throws URISyntaxException, HttpRequestBuilderException {
        verifyPostRequestRequirements();
        URIBuilder uriBuilder = new URIBuilder(uri);
        addQueryParameters(uriBuilder);
        HttpPost httpPost = new HttpPost(uriBuilder.build());
        addHeaders(httpPost);
        addBody(httpPost);
        return new HttpPostRequest(httpPost);
    }

    private HttpGetRequest buildGet() throws URISyntaxException, HttpRequestBuilderException {
        verifyGetRequestRequirements();
        URIBuilder uriBuilder = new URIBuilder(uri);
        addQueryParameters(uriBuilder);
        HttpGet httpGet = new HttpGet(uriBuilder.build());
        addHeaders(httpGet);
        return new HttpGetRequest(httpGet);
    }

    private void addQueryParameters(URIBuilder uriBuilder) {
        queryParameters.forEach((key, value) -> {
            log.debug("setting query parameter " + key + " to " + value);
            uriBuilder.addParameter(key, value);
        });
    }

    private void addHeaders(HttpRequestBase httpRequestBase) {
        headers.forEach((key, value) -> {
            log.debug("setting header " + key + " to " + value);
            httpRequestBase.addHeader(key, value);
        });
    }

    private void addBody(HttpEntityEnclosingRequestBase httpEntityEnclosingRequestBase) {
        getBody().ifPresent(httpEntityEnclosingRequestBase::setEntity);

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

    private void verifyRequestRequirements() throws HttpRequestBuilderException {
        if (uri == null) {
            throw new HttpRequestBuilderException("No uri supplied");
        }
    }

    private Optional<HttpEntity> getBody() {
        return Optional.ofNullable(body);
    }

}
