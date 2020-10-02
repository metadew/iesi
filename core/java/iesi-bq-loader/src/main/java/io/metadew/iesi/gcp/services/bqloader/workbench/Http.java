package io.metadew.iesi.gcp.services.bqloader.workbench;

import io.metadew.iesi.gcp.connection.http.*;

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
                .uri(URI.create("http://127.0.0.1:8080/api/script-executions/b3567a38-abb0-48eb-9e85-649a70e5c2a4"))
                .headers(headers)
                .queryParameters(queryParameters);
        HttpRequest httpRequest = httpRequestBuilder.build();
        HttpRequestService httpRequestService = new HttpRequestService();
        HttpResponse httpResponse = httpRequestService.send(httpRequest);
        String data = httpResponse.getEntityString().orElse("");
        System.out.println(httpResponse.getEntityString().orElse(""));
        //ObjectMapper objectMapper = new ObjectMapper();
        //MyObject myObject = objectMapper.readValue(data,MyObject.class);
        //RepresentationModel representationModel = objectMapper.readValue(data,RepresentationModel.class);
        //ScriptExecutionDto scriptExecutionDto = o1bjectMapper.readValue(data,ScriptExecutionDto.class);
        //System.out.println(objectMapper.writeValueAsString(myObject._embedded));
        //List<ScriptExecutionDto> scriptExecutionDtos = new ArrayList<>();
        //scriptExecutionDtos = objectMapper.readValue(objectMapper.writeValueAsString(myObject._embedded),new TypeReference<List<ScriptExecutionDto>>(){});

        //ScriptExecutionDto scriptExecutionDto = objectMapper.readValue(objectMapper.writeValueAsString(myObject._embedded),ScriptExecutionDto.class);
    }
}
