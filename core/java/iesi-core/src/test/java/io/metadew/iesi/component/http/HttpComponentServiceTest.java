package io.metadew.iesi.component.http;

import io.metadew.iesi.connection.http.HttpConnection;
import io.metadew.iesi.connection.http.HttpConnectionService;
import io.metadew.iesi.connection.http.request.HttpGetRequest;
import io.metadew.iesi.connection.http.request.HttpPostRequest;
import io.metadew.iesi.connection.http.request.HttpRequestBuilderException;
import io.metadew.iesi.script.execution.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.junit.Ignore;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class HttpComponentServiceTest {

    @Test
    void getUriTest() {
        HttpConnection httpConnection = mock(HttpConnection.class);
        HttpConnectionService httpConnectionService = HttpConnectionService.getInstance();
        HttpConnectionService httpConnectionServiceSpy = Mockito.spy(httpConnectionService);
        Whitebox.setInternalState(HttpConnectionService.class, "INSTANCE", httpConnectionServiceSpy);
        doReturn("http://host")
                .when(httpConnectionServiceSpy)
                .getBaseUri(httpConnection);

        assertThat(HttpComponentService.getInstance().getUri(new HttpComponent(
                "component1",
                1L,
                "description",
                httpConnection,
                "/endpoint",
                "get",
                Stream.of(new HttpHeader("content-type", "application/json"), new HttpHeader("content-length", "1000")).collect(Collectors.toList()),
                Stream.of(new HttpQueryParameter("name", "test"), new HttpQueryParameter("version", "2")).collect(Collectors.toList())
        ))).isEqualTo("http://host/endpoint");

        Whitebox.setInternalState(HttpConnectionService.class, "INSTANCE", (HttpConnectionService) null);
    }

    @Test
    void convertTest() {
        HttpConnectionService httpConnectionService = HttpConnectionService.getInstance();
        HttpConnectionService httpConnectionServiceSpy = Mockito.spy(httpConnectionService);
        Whitebox.setInternalState(HttpConnectionService.class, "INSTANCE", httpConnectionServiceSpy);
        HttpHeaderService httpHeaderService = HttpHeaderService.getInstance();
        HttpHeaderService httpHeaderServiceSpy = Mockito.spy(httpHeaderService);
        Whitebox.setInternalState(HttpHeaderService.class, "INSTANCE", httpHeaderServiceSpy);
        HttpQueryParameterService httpQueryParameterService = HttpQueryParameterService.getInstance();
        HttpQueryParameterService httpQueryParameterServiceSpy = Mockito.spy(httpQueryParameterService);
        Whitebox.setInternalState(HttpQueryParameterService.class, "INSTANCE", httpQueryParameterServiceSpy);

        ActionRuntime actionRuntime = mock(ActionRuntime.class);
        ActionControl actionControl = mock(ActionControl.class);
        ExecutionControl executionControl = mock(ExecutionControl.class);
        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);
        ActionExecution actionExecution = mock(ActionExecution.class);

        when(actionExecution.getExecutionControl())
                .thenReturn(executionControl);
        when(executionControl.getExecutionRuntime())
                .thenReturn(executionRuntime);
        when(actionExecution.getActionControl())
                .thenReturn(actionControl);
        when(actionControl.getActionRuntime())
                .thenReturn(actionRuntime);

        when(actionRuntime.resolveRuntimeVariables("/endpoint"))
                .thenReturn("/endpoint");
        when(executionRuntime.resolveVariables(actionExecution, "/endpoint"))
                .thenReturn("/endpoint");
        LookupResult lookupResult = new LookupResult();
        lookupResult.setValue("/endpoint");
        when(executionRuntime.resolveConceptLookup("/endpoint"))
                .thenReturn(lookupResult);
        when(executionRuntime.resolveVariables("/endpoint"))
                .thenReturn("/endpoint");

        when(actionRuntime.resolveRuntimeVariables("get"))
                .thenReturn("get");
        when(executionRuntime.resolveVariables(actionExecution, "get"))
                .thenReturn("get");
        LookupResult getLookupResult = new LookupResult();
        getLookupResult.setValue("get");
        when(executionRuntime.resolveConceptLookup("get"))
                .thenReturn(getLookupResult);
        when(executionRuntime.resolveVariables("get"))
                .thenReturn("get");

        HttpConnection httpConnection = mock(HttpConnection.class);
        doReturn(httpConnection)
                .when(httpConnectionServiceSpy)
                .get("connection1", actionExecution);
        doReturn(new HttpHeader("content-type", "application/json"))
                .when(httpHeaderServiceSpy)
                .convert("content-type,application/json", actionExecution);
        doReturn(new HttpHeader("content-length", "1000"))
                .when(httpHeaderServiceSpy)
                .convert("content-length,1000", actionExecution);
        doReturn(new HttpQueryParameter("name", "test"))
                .when(httpQueryParameterServiceSpy)
                .convert("name,test", actionExecution);
        doReturn(new HttpQueryParameter("version", "2"))
                .when(httpQueryParameterServiceSpy)
                .convert("version,2", actionExecution);

        assertThat(HttpComponentService.getInstance().convert(
                new HttpComponentDefinition(
                        "component1",
                        1L,
                        "description",
                        "connection1",
                        "/endpoint",
                        "get",
                        Stream.of("content-type,application/json", "content-length,1000").collect(Collectors.toList()),
                        Stream.of("name,test", "version,2").collect(Collectors.toList())
                ),
                actionExecution))
                .isEqualTo(
                        new HttpComponent(
                                "component1",
                                1L,
                                "description",
                                httpConnection,
                                "/endpoint",
                                "get",
                                Stream.of(new HttpHeader("content-type", "application/json"), new HttpHeader("content-length", "1000")).collect(Collectors.toList()),
                                Stream.of(new HttpQueryParameter("name", "test"), new HttpQueryParameter("version", "2")).collect(Collectors.toList())
                        ));

        Whitebox.setInternalState(HttpConnectionService.class, "INSTANCE", (HttpConnectionService) null);
        Whitebox.setInternalState(HttpHeaderService.class, "INSTANCE", (HttpHeaderService) null);
        Whitebox.setInternalState(HttpQueryParameterService.class, "INSTANCE", (HttpQueryParameterService) null);
    }

    @Test
    @Disabled
    void buildHttpRequestTest() throws HttpRequestBuilderException, URISyntaxException {
        HttpConnection httpConnection = mock(HttpConnection.class);
        HttpConnectionService httpConnectionService = HttpConnectionService.getInstance();
        HttpConnectionService httpConnectionServiceSpy = Mockito.spy(httpConnectionService);
        Whitebox.setInternalState(HttpConnectionService.class, "INSTANCE", httpConnectionServiceSpy);
        doReturn("http://host")
                .when(httpConnectionServiceSpy)
                .getBaseUri(httpConnection);

        HttpGet httpGet = new HttpGet("http://host/endpoint?name=test&version=2");
        httpGet.addHeader("content-type", "application/json");
        httpGet.addHeader("content-length", "1000");

        assertThat(HttpComponentService.getInstance().buildHttpRequest(new HttpComponent(
                "component1",
                1L,
                "description",
                httpConnection,
                "/endpoint",
                "get",
                Stream.of(new HttpHeader("content-type", "application/json"), new HttpHeader("content-length", "1000")).collect(Collectors.toList()),
                Stream.of(new HttpQueryParameter("name", "test"), new HttpQueryParameter("version", "2")).collect(Collectors.toList())
        ))).isEqualToComparingFieldByField(new HttpGetRequest(httpGet));

        Whitebox.setInternalState(HttpConnectionService.class, "INSTANCE", (HttpConnectionService) null);
    }

    @Test
    @Disabled
    void buildHttpRequestBodyTest() throws HttpRequestBuilderException, URISyntaxException, UnsupportedEncodingException {
        HttpConnection httpConnection = mock(HttpConnection.class);
        HttpConnectionService httpConnectionService = HttpConnectionService.getInstance();
        HttpConnectionService httpConnectionServiceSpy = Mockito.spy(httpConnectionService);
        Whitebox.setInternalState(HttpConnectionService.class, "INSTANCE", httpConnectionServiceSpy);
        doReturn("http://host")
                .when(httpConnectionServiceSpy)
                .getBaseUri(httpConnection);

        HttpPost httpPost = new HttpPost("http://host/endpoint?name=test&version=2");
        httpPost.addHeader("content-type", "application/json");
        httpPost.addHeader("content-length", "1000");
        httpPost.setEntity(new StringEntity("body"));

        assertThat(HttpComponentService.getInstance().buildHttpRequest(new HttpComponent(
                "component1",
                1L,
                "description",
                httpConnection,
                "/endpoint",
                "post",
                Stream.of(new HttpHeader("content-type", "application/json"), new HttpHeader("content-length", "1000")).collect(Collectors.toList()),
                Stream.of(new HttpQueryParameter("name", "test"), new HttpQueryParameter("version", "2")).collect(Collectors.toList())
        ), "body")).isEqualToComparingFieldByField(new HttpPostRequest(httpPost));

        Whitebox.setInternalState(HttpConnectionService.class, "INSTANCE", (HttpConnectionService) null);
    }


}
