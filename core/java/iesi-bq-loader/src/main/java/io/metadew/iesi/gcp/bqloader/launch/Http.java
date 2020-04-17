package io.metadew.iesi.gcp.bqloader.launch;

import io.metadew.iesi.gcp.bqloader.http.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class Http {

    private String uri;
    private Map<String, String> headers;
    private Map<String, String> queryParameters;

    public static void main( String[] args ) throws URISyntaxException, HttpRequestBuilderException, NoSuchAlgorithmException, IOException, KeyManagementException {
        System.out.println("ok");
        HashMap<String, String> headers = new HashMap<>();
        headers.put("User-Agent","Java 8 HttpClient");
        HashMap<String, String> queryParameters = new HashMap<>();
        HttpRequestBuilder httpRequestBuilder = new HttpRequestBuilder()
                .type("GET")
                .uri(URI.create("http://127.0.0.1:8080/api/environments"))
                .headers(headers)
                .queryParameters(queryParameters);
        HttpRequest httpRequest = httpRequestBuilder.build();
        HttpRequestService httpRequestService = new HttpRequestService();
        HttpResponse httpResponse = httpRequestService.send(httpRequest);
        System.out.println(httpResponse.getEntityString().orElse(""));
    }
}
