package io.metadew.iesi.script.action.http;

import io.metadew.iesi.component.http.HttpComponent;
import io.metadew.iesi.component.http.HttpComponentService;
import io.metadew.iesi.component.http.HttpHeader;
import io.metadew.iesi.component.http.HttpQueryParameter;
import io.metadew.iesi.connection.http.HttpConnection;
import io.metadew.iesi.connection.http.HttpConnectionService;
import io.metadew.iesi.connection.http.ProxyConnection;
import io.metadew.iesi.connection.http.request.HttpRequestBuilderException;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.action.key.ActionKey;
import io.metadew.iesi.metadata.definition.action.key.ActionParameterKey;
import io.metadew.iesi.metadata.definition.connection.Connection;
import io.metadew.iesi.metadata.definition.connection.ConnectionParameter;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionKey;
import io.metadew.iesi.metadata.definition.connection.key.ConnectionParameterKey;
import io.metadew.iesi.metadata.definition.environment.key.EnvironmentKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.script.execution.*;
import io.metadew.iesi.script.service.ActionParameterService;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.ConnectionConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import java.net.URISyntaxException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class HttpExecuteRequestTest {

    ExecutionControl executionControl;
    ExecutionRuntime executionRuntime;
    ScriptExecution scriptExecution;
    ActionExecution actionExecution;
    ActionKey actionKey;
    ActionControl actionControl;
    ActionParameterService actionParameterServiceSpy;
    HttpComponentService httpComponentServiceSpy;

    @BeforeEach
    void prepare() {
        executionControl = mock(ExecutionControl.class);
        executionRuntime = mock(ExecutionRuntime.class);
        scriptExecution = mock(ScriptExecution.class);
        actionExecution = mock(ActionExecution.class);
        actionControl = mock(ActionControl.class);
        actionKey = ActionKey.builder()
                .scriptKey(new ScriptKey("scriptId", 1L))
                .actionId("actionId")
                .build();


        when(executionControl.getExecutionRuntime())
                .thenReturn(executionRuntime);
        when(actionExecution.getActionControl())
                .thenReturn(actionControl);

        ActionParameterService actionParameterService = ActionParameterService.getInstance();
        actionParameterServiceSpy = Mockito.spy(actionParameterService);

        HttpComponentService httpComponentService = HttpComponentService.getInstance();
        httpComponentServiceSpy = Mockito.spy(httpComponentService);

        ConnectionConfiguration connectionConfiguration = ConnectionConfiguration.getInstance();

        Whitebox.setInternalState(ActionParameterService.class, "instance", actionParameterServiceSpy);
        Whitebox.setInternalState(HttpComponentService.class, "instance", httpComponentServiceSpy);
    }

    @AfterEach
    void teardown() {
        Whitebox.setInternalState(ActionParameterService.class, "instance",  (ActionParameterService) null);
        Whitebox.setInternalState(HttpComponentService.class, "instance",  (HttpComponentService) null);
    }


    @Test
    void prepareNoHeadersAndNoQueries() throws HttpRequestBuilderException, URISyntaxException {

        ActionParameter requestActionParameter = createActionParameter("request", "request");
        Action action = createAction(requestActionParameter);


        when(actionExecution.getAction())
                .thenReturn(action);

        mockResolvedValues(requestActionParameter);


        HttpComponent httpComponent = createBaseComponent(new ArrayList<>(), new ArrayList<>());
        mockGetAndTraceHttpComponent(httpComponent);

        HttpExecuteRequest httpExecuteRequest = new HttpExecuteRequest(executionControl, scriptExecution, actionExecution);
        httpExecuteRequest.prepare();

        HttpRequestBase httpRequest = httpExecuteRequest.getHttpRequest().getHttpRequest();

        assertThat(httpRequest.getAllHeaders().length).isEqualTo(0);
    }

    @Test
    void prepareDefaultHeaderAndNoQueries() throws HttpRequestBuilderException, URISyntaxException {

        ActionParameter requestActionParameter = createActionParameter("request", "request");
        Action action = createAction(requestActionParameter);
        HttpHeader defaultHeader = HttpHeader.builder().name("Accept").value("application/xml").build();


        when(actionExecution.getAction())
                .thenReturn(action);

        mockResolvedValues(requestActionParameter);


        HttpComponent httpComponent = createBaseComponent(Collections.singletonList(defaultHeader), new ArrayList<>());
        mockGetAndTraceHttpComponent(httpComponent);

        HttpExecuteRequest httpExecuteRequest = new HttpExecuteRequest(executionControl, scriptExecution, actionExecution);
        httpExecuteRequest.prepare();

        HttpRequestBase httpRequest = httpExecuteRequest.getHttpRequest().getHttpRequest();

        assertThat(httpRequest.getAllHeaders().length).isEqualTo(1);
        assertThat(httpRequest.getFirstHeader("Accept").getValue()).isEqualTo("application/xml");

    }

    @Test
    void prepareOverrideHeaderAndNoQueries() throws HttpRequestBuilderException, URISyntaxException {

        ActionParameter requestActionParameter = createActionParameter("request", "request");
        ActionParameter headersActionParameter = createActionParameter("headers", "Accept=application/json");
        Action action = createAction(requestActionParameter, headersActionParameter);
        HttpHeader defaultHeader = HttpHeader.builder().name("Accept").value("application/xml").build();


        when(actionExecution.getAction())
                .thenReturn(action);

        mockResolvedValues(requestActionParameter, headersActionParameter);


        HttpComponent httpComponent = createBaseComponent(Collections.singletonList(defaultHeader), new ArrayList<>());
        mockGetAndTraceHttpComponent(httpComponent);

        HttpExecuteRequest httpExecuteRequest = new HttpExecuteRequest(executionControl, scriptExecution, actionExecution);
        httpExecuteRequest.prepare();

        HttpRequestBase httpRequest = httpExecuteRequest.getHttpRequest().getHttpRequest();

        assertThat(httpRequest.getAllHeaders().length).isEqualTo(1);
        assertThat(httpRequest.getFirstHeader("Accept").getValue()).isEqualTo("application/json");

    }

    @Test
    void prepareHeaderAndNotQueries() throws HttpRequestBuilderException, URISyntaxException {

        ActionParameter requestActionParameter = createActionParameter("request", "request");
        ActionParameter headersActionParameter = createActionParameter("headers", "Accept=application/json");
        Action action = createAction(requestActionParameter, headersActionParameter);


        when(actionExecution.getAction())
                .thenReturn(action);

        mockResolvedValues(requestActionParameter, headersActionParameter);


        HttpComponent httpComponent = createBaseComponent(new ArrayList<>(), new ArrayList<>());
        mockGetAndTraceHttpComponent(httpComponent);

        HttpExecuteRequest httpExecuteRequest = new HttpExecuteRequest(executionControl, scriptExecution, actionExecution);
        httpExecuteRequest.prepare();

        HttpRequestBase httpRequest = httpExecuteRequest.getHttpRequest().getHttpRequest();

        assertThat(httpRequest.getAllHeaders().length).isEqualTo(1);
        assertThat(httpRequest.getFirstHeader("Accept").getValue()).isEqualTo("application/json");
    }

    @Test
    void prepareHeadersAndNoQueries() throws HttpRequestBuilderException, URISyntaxException {

        ActionParameter requestActionParameter = createActionParameter("request", "request");
        ActionParameter headersActionParameter = createActionParameter("headers", "Accept=application/json,X-API-KEY=12345");
        Action action = createAction(requestActionParameter, headersActionParameter);


        when(actionExecution.getAction())
                .thenReturn(action);

        mockResolvedValues(requestActionParameter, headersActionParameter);


        HttpComponent httpComponent = createBaseComponent(new ArrayList<>(), new ArrayList<>());
        mockGetAndTraceHttpComponent(httpComponent);

        HttpExecuteRequest httpExecuteRequest = new HttpExecuteRequest(executionControl, scriptExecution, actionExecution);
        httpExecuteRequest.prepare();

        HttpRequestBase httpRequest = httpExecuteRequest.getHttpRequest().getHttpRequest();

        assertThat(httpRequest.getAllHeaders().length).isEqualTo(2);
        assertThat(httpRequest.getFirstHeader("Accept").getValue()).isEqualTo("application/json");
        assertThat(httpRequest.getFirstHeader("X-API-KEY").getValue()).isEqualTo("12345");
    }

    @Test
    void prepareWrongHeadersAndNoQueries() throws HttpRequestBuilderException, URISyntaxException {

        ActionParameter requestActionParameter = createActionParameter("request", "request");
        ActionParameter headersActionParameter = createActionParameter("headers", "Accept======application/json,X-API-KEY12345");
        Action action = createAction(requestActionParameter, headersActionParameter);


        when(actionExecution.getAction())
                .thenReturn(action);

        mockResolvedValues(requestActionParameter, headersActionParameter);


        HttpComponent httpComponent = createBaseComponent(new ArrayList<>(), new ArrayList<>());
        mockGetAndTraceHttpComponent(httpComponent);

        HttpExecuteRequest httpExecuteRequest = new HttpExecuteRequest(executionControl, scriptExecution, actionExecution);
        httpExecuteRequest.prepare();

        HttpRequestBase httpRequest = httpExecuteRequest.getHttpRequest().getHttpRequest();

        assertThat(httpRequest.getAllHeaders().length).isEqualTo(0);
    }

    @Test
    void prepareDefaultNoHeadersAndQuery() throws HttpRequestBuilderException, URISyntaxException {

        ActionParameter requestActionParameter = createActionParameter("request", "request");
        Action action = createAction(requestActionParameter);
        HttpQueryParameter defaultQueryParam = HttpQueryParameter.builder().name("status").value("sold").build();


        when(actionExecution.getAction())
                .thenReturn(action);

        mockResolvedValues(requestActionParameter);


        HttpComponent httpComponent = createBaseComponent(new ArrayList<>(), Collections.singletonList
                (defaultQueryParam));
        mockGetAndTraceHttpComponent(httpComponent);

        HttpExecuteRequest httpExecuteRequest = new HttpExecuteRequest(executionControl, scriptExecution, actionExecution);
        httpExecuteRequest.prepare();

        HttpRequestBase httpRequest = httpExecuteRequest.getHttpRequest().getHttpRequest();

        assertThat(httpRequest.getURI().toString()).isEqualTo("https://hostendpoint?status=sold");
    }


    @Test
    void prepareOverrideNoHeaderAndQuery() throws HttpRequestBuilderException, URISyntaxException {

        ActionParameter requestActionParameter = createActionParameter("request", "request");
        ActionParameter queryParamsActionParameter = createActionParameter("queryParams", "status=sold");
        Action action = createAction(requestActionParameter, queryParamsActionParameter);
        HttpQueryParameter defaultQueryParams = HttpQueryParameter.builder().name("status").value("available").build();


        when(actionExecution.getAction())
                .thenReturn(action);

        mockResolvedValues(requestActionParameter, queryParamsActionParameter);


        HttpComponent httpComponent = createBaseComponent(new ArrayList<>(), Collections.singletonList(defaultQueryParams));
        mockGetAndTraceHttpComponent(httpComponent);

        HttpExecuteRequest httpExecuteRequest = new HttpExecuteRequest(executionControl, scriptExecution, actionExecution);
        httpExecuteRequest.prepare();

        HttpRequestBase httpRequest = httpExecuteRequest.getHttpRequest().getHttpRequest();

        assertThat(httpRequest.getURI().toString()).isEqualTo("https://hostendpoint?status=sold");
    }

    @Test
    void prepareNoHeadersAndQuery() throws HttpRequestBuilderException, URISyntaxException {

        ActionParameter requestActionParameter = createActionParameter("request", "request");
        ActionParameter queryParamsActionParameter = createActionParameter("queryParams", "status=sold");
        Action action = createAction(requestActionParameter, queryParamsActionParameter);


        when(actionExecution.getAction())
                .thenReturn(action);

        mockResolvedValues(requestActionParameter, queryParamsActionParameter);


        HttpComponent httpComponent = createBaseComponent(new ArrayList<>(), new ArrayList<>());
        mockGetAndTraceHttpComponent(httpComponent);

        HttpExecuteRequest httpExecuteRequest = new HttpExecuteRequest(executionControl, scriptExecution, actionExecution);
        httpExecuteRequest.prepare();

        HttpRequestBase httpRequest = httpExecuteRequest.getHttpRequest().getHttpRequest();

        assertThat(httpRequest.getURI().toString()).isEqualTo("https://hostendpoint?status=sold");
    }

    @Test
    void prepareNoHeadersAndQueries() throws HttpRequestBuilderException, URISyntaxException {

        ActionParameter requestActionParameter = createActionParameter("request", "request");
        ActionParameter queryParamsActionParameter = createActionParameter("queryParams", "status=sold,name=name");
        Action action = createAction(requestActionParameter, queryParamsActionParameter);


        when(actionExecution.getAction())
                .thenReturn(action);

        mockResolvedValues(requestActionParameter, queryParamsActionParameter);


        HttpComponent httpComponent = createBaseComponent(new ArrayList<>(), new ArrayList<>());
        mockGetAndTraceHttpComponent(httpComponent);

        HttpExecuteRequest httpExecuteRequest = new HttpExecuteRequest(executionControl, scriptExecution, actionExecution);
        httpExecuteRequest.prepare();

        HttpRequestBase httpRequest = httpExecuteRequest.getHttpRequest().getHttpRequest();

        assertThat(httpRequest.getURI().toString()).isEqualTo("https://hostendpoint?name=name&status=sold");
    }
    @Test
    void prepareNoHeadersAndWrongQueries() throws HttpRequestBuilderException, URISyntaxException {

        ActionParameter requestActionParameter = createActionParameter("request", "request");
        ActionParameter queryParamsActionParameter = createActionParameter("queryParams", "status=====sold,namename");
        Action action = createAction(requestActionParameter, queryParamsActionParameter);


        when(actionExecution.getAction())
                .thenReturn(action);

        mockResolvedValues(requestActionParameter, queryParamsActionParameter);


        HttpComponent httpComponent = createBaseComponent(new ArrayList<>(), new ArrayList<>());
        mockGetAndTraceHttpComponent(httpComponent);

        HttpExecuteRequest httpExecuteRequest = new HttpExecuteRequest(executionControl, scriptExecution, actionExecution);
        httpExecuteRequest.prepare();

        HttpRequestBase httpRequest = httpExecuteRequest.getHttpRequest().getHttpRequest();

        assertThat(httpRequest.getURI().toString()).isEqualTo("https://hostendpoint");
    }

    @Test
    void prepareNoStatusCode() throws HttpRequestBuilderException, URISyntaxException {

        ActionParameter requestActionParameter = createActionParameter("request", "request");
        Action action = createAction(requestActionParameter);


        when(actionExecution.getAction())
                .thenReturn(action);

        mockResolvedValues(requestActionParameter);


        HttpComponent httpComponent = createBaseComponent(new ArrayList<>(), new ArrayList<>());
        mockGetAndTraceHttpComponent(httpComponent);

        HttpExecuteRequest httpExecuteRequest = new HttpExecuteRequest(executionControl, scriptExecution, actionExecution);
        httpExecuteRequest.prepare();


        assertThat(httpExecuteRequest.getExpectedStatusCodes()).isEmpty();
    }

    @Test
    void prepareStatusCode() throws HttpRequestBuilderException, URISyntaxException {

        ActionParameter requestActionParameter = createActionParameter("request", "request");
        ActionParameter statusCodeActionParameter = createActionParameter("expectedStatusCodes", "200");
        Action action = createAction(requestActionParameter, statusCodeActionParameter);
        List<String> expectedStatusCode = Collections.singletonList("200");


        when(actionExecution.getAction())
                .thenReturn(action);

        mockResolvedValues(requestActionParameter, statusCodeActionParameter);


        HttpComponent httpComponent = createBaseComponent(new ArrayList<>(), new ArrayList<>());
        mockGetAndTraceHttpComponent(httpComponent);

        HttpExecuteRequest httpExecuteRequest = new HttpExecuteRequest(executionControl, scriptExecution, actionExecution);
        httpExecuteRequest.prepare();


        assertThat(httpExecuteRequest.getExpectedStatusCodes()).isNotEmpty();
        assertThat(httpExecuteRequest.getExpectedStatusCodes()).get().isEqualTo(expectedStatusCode);
    }

    @Test
    void prepareMultipleStatusCode() throws HttpRequestBuilderException, URISyntaxException {

        ActionParameter requestActionParameter = createActionParameter("request", "request");
        ActionParameter statusCodeActionParameter = createActionParameter("expectedStatusCodes", "200,400,500");
        Action action = createAction(requestActionParameter, statusCodeActionParameter);
        List<String> expectedStatusCode = new ArrayList<>(Arrays.asList("200", "400", "500"));


        when(actionExecution.getAction())
                .thenReturn(action);

        mockResolvedValues(requestActionParameter, statusCodeActionParameter);


        HttpComponent httpComponent = createBaseComponent(new ArrayList<>(), new ArrayList<>());
        mockGetAndTraceHttpComponent(httpComponent);

        HttpExecuteRequest httpExecuteRequest = new HttpExecuteRequest(executionControl, scriptExecution, actionExecution);
        httpExecuteRequest.prepare();

        assertThat(httpExecuteRequest.getExpectedStatusCodes()).isNotEmpty();
        assertThat(httpExecuteRequest.getExpectedStatusCodes()).get().isEqualTo(expectedStatusCode);
    }

    @Test
    void prepareNoProxyConnection() throws HttpRequestBuilderException, URISyntaxException {

        ActionParameter requestActionParameter = createActionParameter("request", "request");
        Action action = createAction(requestActionParameter);


        when(actionExecution.getAction())
                .thenReturn(action);

        mockResolvedValues(requestActionParameter);


        HttpComponent httpComponent = createBaseComponent(new ArrayList<>(), new ArrayList<>());
        mockGetAndTraceHttpComponent(httpComponent);

        HttpExecuteRequest httpExecuteRequest = new HttpExecuteRequest(executionControl, scriptExecution, actionExecution);
        httpExecuteRequest.prepare();


        assertThat(httpExecuteRequest.getProxyConnection()).isEmpty();
    }


    /*

    @Test
    void prepareProxyConnection() throws HttpRequestBuilderException, URISyntaxException {
        ActionParameter requestActionParameter = createActionParameter("request", "request");
        ActionParameter proxyActionParameter = createActionParameter("proxy", "host");
        Action action = createAction(requestActionParameter, proxyActionParameter);
        ProxyConnection proxyConnection = new ProxyConnection("192.168.1.4", 2000);
        ConnectionKey connectionKey = ConnectionKey.builder().environmentKey(new EnvironmentKey("env0")).name("proxyConection").build();
        ConnectionParameterKey connectionParameterKey = ConnectionParameterKey.builder()
                .connectionKey(connectionKey)
                .parameterName("host").build();

        ConnectionParameter hostParameter = ConnectionParameter.builder().connectionParameterKey(connectionParameterKey).value("host").build();

        Connection connection = Connection.builder()
                .description("connection description")
                .parameters(Arrays.asList(hostParameter)).build();

        when(actionExecution.getAction())
                .thenReturn(action);

        doReturn(Optional.of(connection))
                .when(connectionConfigurationSpy)
                .get(connectionKey);

        mockResolvedValues(requestActionParameter, proxyActionParameter);

        HttpComponent httpComponent = createBaseComponent(new ArrayList<>(), new ArrayList<>());
        mockGetAndTraceHttpComponent(httpComponent);

        HttpExecuteRequest httpExecuteRequest = new HttpExecuteRequest(executionControl, scriptExecution, actionExecution);
        httpExecuteRequest.prepare();


        assertThat(httpExecuteRequest.getProxyConnection()).isNotEmpty();
        assertThat(httpExecuteRequest.getProxyConnection().get()).isEqualTo(proxyConnection);
    }

     */





    ActionParameter createActionParameter(String parameterName, String value) {
        return ActionParameter.builder()
                .actionParameterKey(new ActionParameterKey(actionKey, parameterName))
                .value(value)
                .build();
    }

    Action createAction(ActionParameter... actionParameters) {
        return  Action.builder()
                .actionKey(actionKey)
                .errorExpected("N")
                .errorStop("Y")
                .retries("1")
                .number(0)
                .name("action1")
                .type("http.executeRequest")
                .parameters(Arrays.asList(actionParameters))
                .build();
    }

    void mockResolvedValues(ActionParameter... actionParameters) {
        for (ActionParameter actionParameter : actionParameters) {
            doReturn(new Text(actionParameter.getValue()))
                    .when(actionParameterServiceSpy)
                    .getValue(actionParameter, executionRuntime, actionExecution);
        }
    }
    void mockGetAndTraceHttpComponent(HttpComponent httpComponent) {
        doReturn(httpComponent)
                .when(httpComponentServiceSpy)
                .getAndTrace("request", actionExecution, "request");
    }

    HttpComponent createBaseComponent(List<HttpHeader> headers, List<HttpQueryParameter> queryParams) {
        return new HttpComponent("request",
                1L,
                "description",
                new HttpConnection(
                        "request_connection",
                        "description",
                        "environment",
                        "host",
                        null,
                        null,
                        true
                ),
                "endpoint",
                "GET",
                headers,
                queryParams);
    }

}
