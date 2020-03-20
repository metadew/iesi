package io.metadew.iesi.metadata.definition;

import io.metadew.iesi.datatypes.DataType;
import io.metadew.iesi.datatypes.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestComponent {

    private String uri;
    private Map<String, String> headers;
    private Map<String, String> queryParameters;

    private final static Logger LOGGER = LogManager.getLogger();

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
        headers.forEach((header, headerValue) ->
                        headersConverted.put(header, convertHeader(headerValue)));
        return headersConverted;
    }

    private String convertHeader(DataType header) {
        if (header == null) {
            return "";
        } else if (header instanceof Text) {
            return ((Text) header).getString();
        } else {
            LOGGER.warn(MessageFormat.format("Http request does not accept ''{0}'' as header type", header.getClass()));
            return "";
        }
    }

    private String convertQueryParameter(DataType queryParameter) {
        if(queryParameter == null) {
            return "";
        } else if (queryParameter instanceof Text) {
            return ((Text) queryParameter).getString();
        } else {
            LOGGER.warn(MessageFormat.format("Http request does not accept ''{0}'' as query parameter type", queryParameter.getClass()));
            return "";
        }
    }

    private String convertUri(DataType uri) {
        if(uri == null) {
            return "";
        } else if (uri instanceof Text) {
            return ((Text) uri).getString();
        } else {
            LOGGER.warn(MessageFormat.format("Http request does not accept ''{0}'' as uri type", uri.getClass()));
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
