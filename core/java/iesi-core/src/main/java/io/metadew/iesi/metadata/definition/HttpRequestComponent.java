package io.metadew.iesi.metadata.definition;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.Text;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestComponent {

    private String uri;
    private Map<String, String> headers;
    private Map<String, String> queryParameters;

    public HttpRequestComponent(String uri, Map<String, String> headers, Map<String, String> queryParameters) {
        this.uri = uri;
        this.headers = headers;
        this.queryParameters = queryParameters;
    }

    public HttpRequestComponent(DataType uri, Map<String, DataType> headers, Map<String, DataType> queryParameters) {
        this.uri = convertUri(uri);
        this.headers = convertHeaders(headers);
        this.queryParameters = convertQueryParameters(queryParameters);
    }

    private Map<String, String> convertQueryParameters(Map<String, DataType> queryParameters) {
        HashMap<String, String> queryParametersConverted = new HashMap<>();
        queryParameters
                .forEach((queryParameter, queryParameterValue) ->
                        queryParametersConverted.put(queryParameter, convertQueryParameter(queryParameterValue)));
        return queryParametersConverted;
    }

    private Map<String, String> convertHeaders(Map<String, DataType> headers) {
        HashMap<String, String> headersConverted = new HashMap<>();
        headers
                .forEach((header, headerValue) ->
                        headersConverted.put(header, convertHeader(headerValue)));
        return headersConverted;
    }

    private String convertHeader(DataType header) {
        if(header == null) {
            return "";
        } else if (header instanceof Text) {
            return ((Text) header).getString();
        } else {
            // TODO: framework log
            return "";
        }
    }

    private String convertQueryParameter(DataType queryParameter) {
        if(queryParameter == null) {
            return "";
        } else if (queryParameter instanceof Text) {
            return ((Text) queryParameter).getString();
        } else {
            // TODO: framework log
            return "";
        }
    }

    private String convertUri(DataType uri) {
        if(uri == null) {
            return "";
        } else if (uri instanceof Text) {
            return ((Text) uri).getString();
        } else {
            // TODO: framework log
            return "";
        }
    }

    public String getUri() {
        return uri;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }
}
