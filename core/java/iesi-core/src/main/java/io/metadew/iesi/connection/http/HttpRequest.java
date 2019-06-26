package io.metadew.iesi.connection.http;

import org.apache.http.client.utils.URIBuilder;

import java.net.URISyntaxException;
import java.util.HashMap;

/**
 * Object for managing the http request to be used in the http connection object.
 *
 * @author peter.billen
 */
public class HttpRequest {

    private String url;

    private URIBuilder uriBuilder;

    private HashMap<String, String> headerMap;

    // Constructor
    public HttpRequest() {
        super();
        this.setHeaderMap(new HashMap<String, String>());
    }

    public HttpRequest(String url) {
        super();
        this.setUrl(url);
        this.setHeaderMap(new HashMap<String, String>());
    }

    // Methods
    public void addQueryParam(String paramKey, String paramValue) {
        this.getUriBuilder().setParameter(paramKey, paramValue);
    }

    public void addHeader(String headerKey, String headerValue) {
        this.getHeaderMap().put(headerKey, headerValue);
    }

    // Getters and setters
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;

        try {
            this.setUriBuilder(new URIBuilder(this.url));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    public URIBuilder getUriBuilder() {
        return uriBuilder;
    }

    public void setUriBuilder(URIBuilder uriBuilder) {
        this.uriBuilder = uriBuilder;
    }

    public HashMap<String, String> getHeaderMap() {
        return headerMap;
    }

    public void setHeaderMap(HashMap<String, String> headerMap) {
        this.headerMap = headerMap;
    }

}