package io.metadew.iesi.component.http;

import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.connection.http.HttpConnection;
import io.metadew.iesi.connection.http.HttpConnectionService;
import io.metadew.iesi.connection.http.request.HttpGetRequest;
import io.metadew.iesi.connection.http.request.HttpPostRequest;
import io.metadew.iesi.connection.http.request.HttpRequestBuilderException;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.metadata.configuration.action.design.ActionParameterDesignTraceConfiguration;
import io.metadew.iesi.metadata.configuration.action.trace.ActionParameterTraceConfiguration;
import io.metadew.iesi.metadata.configuration.component.ComponentAttributeConfiguration;
import io.metadew.iesi.metadata.configuration.component.ComponentConfiguration;
import io.metadew.iesi.metadata.configuration.component.ComponentParameterConfiguration;
import io.metadew.iesi.metadata.configuration.component.ComponentVersionConfiguration;
import io.metadew.iesi.metadata.configuration.component.trace.ComponentDesignTraceConfiguration;
import io.metadew.iesi.metadata.configuration.component.trace.ComponentTraceConfiguration;
import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.configuration.connection.ConnectionParameterConfiguration;
import io.metadew.iesi.metadata.configuration.connection.trace.ConnectionTraceConfiguration;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.action.design.ActionParameterDesignTrace;
import io.metadew.iesi.metadata.definition.action.design.key.ActionParameterDesignTraceKey;
import io.metadew.iesi.metadata.definition.action.key.ActionKey;
import io.metadew.iesi.metadata.definition.component.Component;
import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.metadata.definition.component.ComponentVersion;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentParameterKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentVersionKey;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.script.Script;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.metadata.definition.security.SecurityGroupKey;
import io.metadew.iesi.metadata.service.action.ActionParameterTraceService;
import io.metadew.iesi.metadata.service.connection.trace.http.HttpConnectionTraceService;
import io.metadew.iesi.metadata.service.metadata.MetadataFieldService;
import io.metadew.iesi.script.execution.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class, HttpComponentService.class, ComponentConfiguration.class, ActionParameterTraceConfiguration.class, ActionParameterDesignTraceConfiguration.class, ActionParameterTraceService.class,
        HttpConnectionService.class, HttpComponentTraceService.class, HttpConnectionTraceService.class, HttpComponentDefinitionService.class, HttpQueryParameterService.class,
        DataTypeHandler.class, ComponentVersionConfiguration.class, ComponentParameterConfiguration.class, ComponentAttributeConfiguration.class, ComponentTraceConfiguration.class,
        ConnectionTraceConfiguration.class, HttpComponentDesignTraceService.class, ComponentDesignTraceConfiguration.class, MetadataFieldService.class, ConnectionConfiguration.class,
        ConnectionParameterConfiguration.class})
@ActiveProfiles("test")
class HttpComponentServiceTest {


    @Autowired
    ConnectionConfiguration connectionConfiguration;

    @MockBean
    ComponentConfiguration componentConfiguration;

    @MockBean
    HttpConnection httpConnection;

    @MockBean
    HttpConnectionService httpConnectionService;

    @MockBean
    ConnectionTraceConfiguration connectionTraceConfiguration;

    @MockBean
    ActionParameterDesignTraceConfiguration actionParameterDesignTraceConfiguration;

    @MockBean
    HttpComponentDefinitionService httpComponentDefinitionService;

    @MockBean
    HttpConnectionTraceService httpConnectionTraceService;

    @MockBean
    HttpComponentTraceService httpComponentTraceService;

    @SpyBean
    HttpComponentService httpComponentService;

    @SpyBean
    HttpConnectionService httpConnectionServiceSpy;

    @SpyBean
    HttpHeaderService httpHeaderServiceSpy;

    @SpyBean
    HttpQueryParameterService httpQueryParameterServiceSpy;

    @Test
    void getUriTest() {
        HttpConnection httpConnection = mock(HttpConnection.class);

        doReturn("http://host")
                .when(httpConnectionServiceSpy)
                .getBaseUri(httpConnection);

        assertThat(httpComponentService.getUri(new HttpComponent(
                "component1",
                1L,
                "description",
                httpConnection,
                "/endpoint",
                "get",
                Stream.of(new HttpHeader("content-type", "application/json"), new HttpHeader("content-length", "1000")).collect(Collectors.toList()),
                Stream.of(new HttpQueryParameter("name", "test"), new HttpQueryParameter("version", "2")).collect(Collectors.toList())
        ))).isEqualTo("http://host/endpoint");
    }

    @Test
    void convertTest() {
        ActionRuntime actionRuntime = mock(ActionRuntime.class);
        ActionControl actionControl = mock(ActionControl.class);
        ExecutionControl executionControl = mock(ExecutionControl.class);
        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);
        ActionExecution actionExecution = mock(ActionExecution.class);

        when(actionExecution.getExecutionControl())
                .thenReturn(executionControl);
        when(actionExecution.getExecutionControl().getRunId())
                .thenReturn("runId");
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
        when(httpConnection.getReferenceName())
                .thenReturn("connectionName");
        doReturn(httpConnection)
                .when(httpConnectionServiceSpy)
                .get("connection1", actionExecution);
        doReturn(new HttpHeader("content-type", "application/json"))
                .when(httpHeaderServiceSpy)
                .convert(new HttpHeaderDefinition("content-type", "application/json"), actionExecution);
        doReturn(new HttpHeader("content-length", "1000"))
                .when(httpHeaderServiceSpy)
                .convert(new HttpHeaderDefinition("content-length", "1000"), actionExecution);
        doReturn(new HttpQueryParameter("name", "test"))
                .when(httpQueryParameterServiceSpy)
                .convert(new HttpQueryParameterDefinition("name", "test"), actionExecution);
        doReturn(new HttpQueryParameter("version", "2"))
                .when(httpQueryParameterServiceSpy)
                .convert(new HttpQueryParameterDefinition("version", "2"), actionExecution);

        assertThat(httpComponentService.convert(
                new HttpComponentDefinition(
                        "component1",
                        1L,
                        "description",
                        "connection1",
                        "/endpoint",
                        "get",
                        Stream.of(new HttpHeaderDefinition("content-type", "application/json"), new HttpHeaderDefinition("content-length", "1000")).collect(Collectors.toList()),
                        Stream.of(new HttpQueryParameterDefinition("name", "test"), new HttpQueryParameterDefinition("version", "2")).collect(Collectors.toList())
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
    }

    @Test
    void getAndTraceTestRightNameAndVersion() {
        ActionExecution actionExecution = mock(ActionExecution.class);
        ExecutionControl executionControl = mock(ExecutionControl.class);


        long componentVersion1 = 0L;
        ComponentKey componentKey = new ComponentKey(UUID.randomUUID().toString(), componentVersion1);

        when(actionExecution.getExecutionControl()).thenReturn(executionControl);
        when(executionControl.getProcessId()).thenReturn(1L);
        when(executionControl.getRunId()).thenReturn("1");
        when(executionControl.getEnvName()).thenReturn("env0");
        when(actionParameterDesignTraceConfiguration.get(any()))
                .thenReturn(Optional.of(new ActionParameterDesignTrace(null, null)));
        when(componentConfiguration.getByNameAndVersion("component1", 0L))
                .thenReturn(Optional.of(new Component(
                        componentKey,
                        new SecurityGroupKey(UUID.randomUUID()),
                        "PUBLIC",
                        "http.request",
                        "component1",
                        "description",
                        new ComponentVersion(new ComponentVersionKey(componentKey), "description"),
                        Stream.of(
                                new ComponentParameter(new ComponentParameterKey(componentKey, "connection"), "connectionName"),
                                new ComponentParameter(new ComponentParameterKey(componentKey, "endpoint"), "/pet"),
                                new ComponentParameter(new ComponentParameterKey(componentKey, "type"), "GET")
                        ).collect(Collectors.toList()),
                        new ArrayList<>()
                )));
        when(httpComponentDefinitionService.convertAndTrace(any(), any(), any()))
                .thenReturn(new HttpComponentDefinition(
                        "component1",
                        0L,
                        "description",
                        "connection",
                        "endpoint",
                        "type",
                        new ArrayList<>(),
                        new ArrayList<>()
                ));
        when(httpConnectionService.get(any(), any()))
                .thenReturn(new HttpConnection(
                        "connectionName",
                        "description",
                        "env0",
                        "http://test.com",
                        "/api",
                        8080,
                        false
                ));


        doReturn("/pet").when(httpComponentService).resolveEndpoint(anyString(), any(ActionExecution.class));
        doReturn("GET").when(httpComponentService).resolveType(anyString(), any(ActionExecution.class));


        doNothing()
                .when(connectionTraceConfiguration)
                .insert(any());

        doNothing()
                .when(actionParameterDesignTraceConfiguration)
                .insert(any(ActionParameterDesignTrace.class));

        HttpComponent httpComponent1 = new HttpComponent(
                "component1",
                componentVersion1,
                "description",
                new HttpConnection(
                        "connectionName",
                        "description",
                        "env0",
                        "http://test.com",
                        "/api",
                        8080,
                        false
                ),
                "/pet",
                "GET",
                new ArrayList<>(),
                new ArrayList<>()
        );


        assertThat(httpComponentService.getAndTrace("component1", actionExecution, "request", 0L)).isEqualTo(httpComponent1);
    }

    @Test
    void getAndTraceTestNameAndUnknownVersion() {
        ActionExecution actionExecution = mock(ActionExecution.class);
        ExecutionControl executionControl = mock(ExecutionControl.class);

        when(actionExecution.getExecutionControl()).thenReturn(executionControl);
        when(executionControl.getProcessId()).thenReturn(1L);
        when(executionControl.getRunId()).thenReturn("1");
        when(executionControl.getEnvName()).thenReturn("env0");

        Mockito.doReturn("/pet").when(httpComponentService).resolveEndpoint(anyString(), any(ActionExecution.class));
        Mockito.doReturn("GET").when(httpComponentService).resolveType(anyString(), any(ActionExecution.class));

        Mockito.doReturn(Optional.empty())
                .when(componentConfiguration)
                .getByNameAndVersion("component1", 3L);


        assertThatThrownBy(() -> {
            httpComponentService.getAndTrace("component1", actionExecution, "request", 3L);
        }).isInstanceOf(RuntimeException.class).hasMessage("Could not find http component with name component1 and version 3");
    }

    @Test
    void getAndTraceTestNameAndNoVersion() {
        ActionExecution actionExecution = mock(ActionExecution.class);
        ExecutionControl executionControl = mock(ExecutionControl.class);
        ScriptExecution scriptExecution = mock(ScriptExecution.class);

        ScriptKey scriptKey = new ScriptKey("1", 1L);
        ComponentKey componentKey = new ComponentKey(UUID.randomUUID().toString(), 3L);

        Script script = Script.builder()
                .scriptKey(scriptKey)
                .build();
        Action action = Action.builder()
                .actionKey(new ActionKey(scriptKey, "1"))
                .retries("0")
                .build();

        Long componentVersion3 = 3L;

        HttpComponent httpComponent3 = new HttpComponent(
                "component1",
                componentVersion3,
                "description",
                new HttpConnection(
                        "connectionName",
                        "description",
                        "env0",
                        "http://test.com",
                        "/api",
                        8080,
                        false
                ),
                "/pet",
                "POST",
                new ArrayList<>(),
                new ArrayList<>()
        );

        when(actionExecution.getExecutionControl()).thenReturn(executionControl);
        when(actionExecution.getScriptExecution()).thenReturn(scriptExecution);
        when(executionControl.getProcessId()).thenReturn(1L);
        when(executionControl.getRunId()).thenReturn("1");
        when(executionControl.getEnvName()).thenReturn("env0");
        when(scriptExecution.getScript()).thenReturn(script);
        when(actionExecution.getAction()).thenReturn(action);
        when(componentConfiguration.getByNameAndLatestVersion("component1"))
                .thenReturn(Optional.of(new Component(
                        componentKey,
                        new SecurityGroupKey(UUID.randomUUID()),
                        "PUBLIC",
                        "http.request",
                        "component1",
                        "description",
                        new ComponentVersion(new ComponentVersionKey(componentKey), "description"),
                        Stream.of(
                                new ComponentParameter(new ComponentParameterKey(componentKey, "connection"), "connectionName"),
                                new ComponentParameter(new ComponentParameterKey(componentKey, "endpoint"), "/pet"),
                                new ComponentParameter(new ComponentParameterKey(componentKey, "type"), "POST")
                        ).collect(Collectors.toList()),
                        new ArrayList<>()
                )));

        when(httpComponentDefinitionService.convertAndTrace(any(), any(), any()))
                .thenReturn(new HttpComponentDefinition(
                        "component1",
                        3L,
                        "description",
                        "connection",
                        "endpoint",
                        "type",
                        new ArrayList<>(),
                        new ArrayList<>()
                ));

        when(httpConnectionService.get(any(), any()))
                .thenReturn(new HttpConnection(
                        "connectionName",
                        "description",
                        "env0",
                        "http://test.com",
                        "/api",
                        8080,
                        false
                ));

        doReturn("/pet").when(httpComponentService).resolveEndpoint(anyString(), any(ActionExecution.class));
        doReturn("POST").when(httpComponentService).resolveType(anyString(), any(ActionExecution.class));

        doNothing()
                .when(httpConnectionTraceService)
                .trace(any(), any(), any());
        doNothing()
                .when(httpComponentTraceService)
                .trace(any(), any(), any());


        assertThat(httpComponentService.getAndTrace("component1", actionExecution, "request", "requestVersion")).isEqualTo(httpComponent3);
    }

    @Disabled
    void buildHttpRequestTest() throws HttpRequestBuilderException, URISyntaxException {
        doReturn("http://host")
                .when(httpConnectionServiceSpy)
                .getBaseUri(httpConnection);

        HttpGet httpGet = new HttpGet("http://host/endpoint?name=test&version=2");
        httpGet.addHeader("content-type", "application/json");
        httpGet.addHeader("content-length", "1000");

        assertThat(httpComponentService.buildHttpRequest(new HttpComponent(
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

    @Disabled
    void buildHttpRequestBodyTest() throws HttpRequestBuilderException, URISyntaxException, UnsupportedEncodingException {
        Whitebox.setInternalState(HttpConnectionService.class, "INSTANCE", httpConnectionServiceSpy);
        doReturn("http://host")
                .when(httpConnectionServiceSpy)
                .getBaseUri(httpConnection);

        HttpPost httpPost = new HttpPost("http://host/endpoint?name=test&version=2");
        httpPost.addHeader("content-type", "application/json");
        httpPost.addHeader("content-length", "1000");
        httpPost.setEntity(new StringEntity("body"));

        assertThat(httpComponentService.buildHttpRequest(new HttpComponent(
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
