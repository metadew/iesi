package io.metadew.iesi.script.action.http;

import io.metadew.iesi.component.http.HttpComponent;
import io.metadew.iesi.component.http.HttpComponentService;
import io.metadew.iesi.component.http.HttpHeader;
import io.metadew.iesi.component.http.HttpQueryParameter;
import io.metadew.iesi.connection.http.HttpConnection;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.action.key.ActionKey;
import io.metadew.iesi.metadata.definition.action.key.ActionParameterKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.script.execution.*;
import io.metadew.iesi.script.service.ActionParameterService;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class HttpExecuteRequestTest {

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

        ActionParameterService actionParameterService = ActionParameterService.getInstance();
        actionParameterServiceSpy = Mockito.spy(actionParameterService);

        HttpComponentService httpComponentService = HttpComponentService.getInstance();
        httpComponentServiceSpy = Mockito.spy(httpComponentService);

        when(executionControl.getExecutionRuntime())
                .thenReturn(executionRuntime);
        when(actionExecution.getActionControl())
                .thenReturn(actionControl);
        doNothing().when(httpComponentServiceSpy).traceEmptyVersion(actionExecution, "", 0L);


        Whitebox.setInternalState(ActionParameterService.class, "instance", actionParameterServiceSpy);
        Whitebox.setInternalState(HttpComponentService.class, "instance", httpComponentServiceSpy);
    }

    @AfterEach
    void teardown() {
        Whitebox.setInternalState(ActionParameterService.class, "instance", (ActionParameterService) null);
        Whitebox.setInternalState(HttpComponentService.class, "instance", (HttpComponentService) null);
    }

    @Test
    void prepareNoHeadersAndNoQueries() throws Exception {

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

        assertThat(httpRequest.getAllHeaders()).isEmpty();
        assertThat(httpExecuteRequest.getExpectedStatusCodes()).isEmpty();
        assertThat(httpExecuteRequest.getProxyConnection()).isEmpty();
    }

    @Test
    void prepareDefaultHeaderAndNoQueries() throws Exception {

        ActionParameter requestActionParameter = createActionParameter("request", "request");
        ActionParameter headersActionParameter = createActionParameter("headers", "X-API-KEY=\"1234\"");
        Action action = createAction(requestActionParameter, headersActionParameter);
        HttpHeader defaultHeader = new HttpHeader("Accept", "application/xml");

        when(actionExecution.getAction())
                .thenReturn(action);

        mockResolvedValues(requestActionParameter, headersActionParameter);

        HttpComponent httpComponent = createBaseComponent(Collections.singletonList(defaultHeader), new ArrayList<>());
        mockGetAndTraceHttpComponent(httpComponent);

        HttpExecuteRequest httpExecuteRequest = new HttpExecuteRequest(executionControl, scriptExecution, actionExecution);
        httpExecuteRequest.prepare();

        HttpRequestBase httpRequest = httpExecuteRequest.getHttpRequest().getHttpRequest();

        assertThat(httpRequest.getAllHeaders()).hasSize(2);
        assertThat(httpRequest.getFirstHeader("Accept").getValue()).isEqualTo("application/xml");
        assertThat(httpRequest.getFirstHeader("X-API-KEY").getValue()).isEqualTo("1234");
    }

    @Test
    void prepareOverrideHeaderAndNoQueries() throws Exception {

        ActionParameter requestActionParameter = createActionParameter("request", "request");
        ActionParameter headersActionParameter = createActionParameter("headers", "Accept=\"application/xml\"");
        Action action = createAction(requestActionParameter, headersActionParameter);
        HttpHeader existingHeader = new HttpHeader("Accept", "application/json");

        when(actionExecution.getAction())
                .thenReturn(action);

        mockResolvedValues(requestActionParameter, headersActionParameter);

        HttpComponent httpComponent = createBaseComponent(Collections.singletonList(existingHeader), new ArrayList<>());
        mockGetAndTraceHttpComponent(httpComponent);

        HttpExecuteRequest httpExecuteRequest = new HttpExecuteRequest(executionControl, scriptExecution, actionExecution);
        httpExecuteRequest.prepare();

        HttpRequestBase httpRequest = httpExecuteRequest.getHttpRequest().getHttpRequest();

        assertThat(httpRequest.getAllHeaders()).hasSize(1);
        assertThat(httpRequest.getFirstHeader("Accept").getValue()).isEqualTo("application/xml");
    }


    @Test
    void prepareHeaderAndNoQueries() throws Exception {

        ActionParameter requestActionParameter = createActionParameter("request", "request");
        ActionParameter headersActionParameter = createActionParameter("headers", "Accept=\"application/json\"");
        Action action = createAction(requestActionParameter, headersActionParameter);

        when(actionExecution.getAction())
                .thenReturn(action);

        mockResolvedValues(requestActionParameter, headersActionParameter);

        HttpComponent httpComponent = createBaseComponent(new ArrayList<>(), new ArrayList<>());
        mockGetAndTraceHttpComponent(httpComponent);

        HttpExecuteRequest httpExecuteRequest = new HttpExecuteRequest(executionControl, scriptExecution, actionExecution);
        httpExecuteRequest.prepare();

        HttpRequestBase httpRequest = httpExecuteRequest.getHttpRequest().getHttpRequest();

        assertThat(httpRequest.getAllHeaders()).hasSize(1);
        assertThat(httpRequest.getFirstHeader("Accept").getValue()).isEqualTo("application/json");
    }

    @Test
    void prepareHeadersAndNoQuery() throws Exception {

        ActionParameter requestActionParameter = createActionParameter("request", "request");
        ActionParameter headersActionParameter = createActionParameter("headers", "Accept=\"application/json;version=1.2\",Content-Type=\"application/json,application/xml,application/yml\"");
        Action action = createAction(requestActionParameter, headersActionParameter);

        when(actionExecution.getAction())
                .thenReturn(action);

        mockResolvedValues(requestActionParameter, headersActionParameter);

        HttpComponent httpComponent = createBaseComponent(new ArrayList<>(), new ArrayList<>());
        mockGetAndTraceHttpComponent(httpComponent);

        HttpExecuteRequest httpExecuteRequest = new HttpExecuteRequest(executionControl, scriptExecution, actionExecution);
        httpExecuteRequest.prepare();

        HttpRequestBase httpRequest = httpExecuteRequest.getHttpRequest().getHttpRequest();

        assertThat(httpRequest.getAllHeaders()).hasSize(2);
        assertThat(httpRequest.getFirstHeader("Accept").getValue()).isEqualTo("application/json;version=1.2");
        assertThat(httpRequest.getFirstHeader("Content-Type").getValue()).isEqualTo("application/json,application/xml,application/yml");
    }

    @Test
    void prepareWrongHeadersAndNoQueries() {

        ActionParameter requestActionParameter = createActionParameter("request", "request");
        ActionParameter headersActionParameter = createActionParameter("headers", "Accept======\"application/json\",Content-Type\"application/json,application/xml,application/yml\"");
        Action action = createAction(requestActionParameter, headersActionParameter);

        when(actionExecution.getAction())
                .thenReturn(action);

        mockResolvedValues(requestActionParameter, headersActionParameter);

        HttpComponent httpComponent = createBaseComponent(new ArrayList<>(), new ArrayList<>());
        mockGetAndTraceHttpComponent(httpComponent);

        HttpExecuteRequest httpExecuteRequest = new HttpExecuteRequest(executionControl, scriptExecution, actionExecution);

        assertThrows(QuoteCharException.class, httpExecuteRequest::prepare);
    }

    @Test
    void prepareWrongHeadersNoFirstQuoteAndNoQueries() {

        ActionParameter requestActionParameter = createActionParameter("request", "request");
        ActionParameter headersActionParameter = createActionParameter("headers", "Accept=application/json\", Content-Type=application/json,application/xml,application/yml\"");
        Action action = createAction(requestActionParameter, headersActionParameter);

        when(actionExecution.getAction())
                .thenReturn(action);

        mockResolvedValues(requestActionParameter, headersActionParameter);

        HttpComponent httpComponent = createBaseComponent(new ArrayList<>(), new ArrayList<>());
        mockGetAndTraceHttpComponent(httpComponent);

        HttpExecuteRequest httpExecuteRequest = new HttpExecuteRequest(executionControl, scriptExecution, actionExecution);

        assertThrows(QuoteCharException.class, httpExecuteRequest::prepare);
    }

    @Test
    void prepareWrongHeadersNoLastQuoteAndNoQueries() {

        ActionParameter requestActionParameter = createActionParameter("request", "request");
        ActionParameter headersActionParameter = createActionParameter("headers", "Accept=\"application/json\", Content-Type=\"application/json,application/xml,application/yml");
        Action action = createAction(requestActionParameter, headersActionParameter);

        when(actionExecution.getAction())
                .thenReturn(action);

        mockResolvedValues(requestActionParameter, headersActionParameter);

        HttpComponent httpComponent = createBaseComponent(new ArrayList<>(), new ArrayList<>());
        mockGetAndTraceHttpComponent(httpComponent);

        HttpExecuteRequest httpExecuteRequest = new HttpExecuteRequest(executionControl, scriptExecution, actionExecution);

        assertThrows(QuoteCharException.class, httpExecuteRequest::prepare);
    }

    @Test
    void prepareDefaultNoHeadersAndQuery() throws Exception {

        ActionParameter requestActionParameter = createActionParameter("request", "request");
        ActionParameter queryParameterActionParameter = createActionParameter("queryParameters", "name=name");
        Action action = createAction(requestActionParameter, queryParameterActionParameter);
        HttpQueryParameter defaultQueryParam = new HttpQueryParameter("status", "sold");

        when(actionExecution.getAction())
                .thenReturn(action);

        mockResolvedValues(requestActionParameter, queryParameterActionParameter);

        HttpComponent httpComponent = createBaseComponent(new ArrayList<>(), Collections.singletonList
                (defaultQueryParam));
        mockGetAndTraceHttpComponent(httpComponent);

        HttpExecuteRequest httpExecuteRequest = new HttpExecuteRequest(executionControl, scriptExecution, actionExecution);
        httpExecuteRequest.prepare();

        HttpRequestBase httpRequest = httpExecuteRequest.getHttpRequest().getHttpRequest();

        assertThat(httpRequest.getURI()).hasToString("https://hostendpoint?name=name&status=sold");
    }


    @Test
    void prepareOverrideNoHeaderAndQuery() throws Exception {

        ActionParameter requestActionParameter = createActionParameter("request", "request");
        ActionParameter queryParametersActionParameter = createActionParameter("queryParameters", "status=sold");
        Action action = createAction(requestActionParameter, queryParametersActionParameter);
        HttpQueryParameter defaultQueryParameters = new HttpQueryParameter("status", "available");

        when(actionExecution.getAction())
                .thenReturn(action);

        mockResolvedValues(requestActionParameter, queryParametersActionParameter);

        HttpComponent httpComponent = createBaseComponent(new ArrayList<>(), Collections.singletonList(defaultQueryParameters));
        mockGetAndTraceHttpComponent(httpComponent);

        HttpExecuteRequest httpExecuteRequest = new HttpExecuteRequest(executionControl, scriptExecution, actionExecution);
        httpExecuteRequest.prepare();

        HttpRequestBase httpRequest = httpExecuteRequest.getHttpRequest().getHttpRequest();

        assertThat(httpRequest.getURI()).hasToString("https://hostendpoint?status=sold");
    }


    @Test
    void prepareNoHeadersAndQuery() throws Exception {

        ActionParameter requestActionParameter = createActionParameter("request", "request");
        ActionParameter queryParametersActionParameter = createActionParameter("queryParameters", "status=sold");
        Action action = createAction(requestActionParameter, queryParametersActionParameter);

        when(actionExecution.getAction())
                .thenReturn(action);

        mockResolvedValues(requestActionParameter, queryParametersActionParameter);

        HttpComponent httpComponent = createBaseComponent(new ArrayList<>(), new ArrayList<>());
        mockGetAndTraceHttpComponent(httpComponent);

        HttpExecuteRequest httpExecuteRequest = new HttpExecuteRequest(executionControl, scriptExecution, actionExecution);
        httpExecuteRequest.prepare();

        HttpRequestBase httpRequest = httpExecuteRequest.getHttpRequest().getHttpRequest();

        assertThat(httpRequest.getURI()).hasToString("https://hostendpoint?status=sold");
    }

    @Test
    void prepareNoHeadersAndQueries() throws Exception {

        ActionParameter requestActionParameter = createActionParameter("request", "request");
        ActionParameter queryParametersActionParameter = createActionParameter("queryParameters", "status=sold,name=name");
        Action action = createAction(requestActionParameter, queryParametersActionParameter);

        when(actionExecution.getAction())
                .thenReturn(action);

        mockResolvedValues(requestActionParameter, queryParametersActionParameter);

        HttpComponent httpComponent = createBaseComponent(new ArrayList<>(), new ArrayList<>());
        mockGetAndTraceHttpComponent(httpComponent);

        HttpExecuteRequest httpExecuteRequest = new HttpExecuteRequest(executionControl, scriptExecution, actionExecution);
        httpExecuteRequest.prepare();

        HttpRequestBase httpRequest = httpExecuteRequest.getHttpRequest().getHttpRequest();

        assertThat(httpRequest.getURI()).hasToString("https://hostendpoint?name=name&status=sold");
    }


    @Test
    void prepareNoHeadersAndWrongQueries() {

        ActionParameter requestActionParameter = createActionParameter("request", "request");
        ActionParameter queryParametersActionParameter = createActionParameter("queryParameters", "status=====sold,namename");
        Action action = createAction(requestActionParameter, queryParametersActionParameter);

        when(actionExecution.getAction())
                .thenReturn(action);

        mockResolvedValues(requestActionParameter, queryParametersActionParameter);

        HttpComponent httpComponent = createBaseComponent(new ArrayList<>(), new ArrayList<>());
        mockGetAndTraceHttpComponent(httpComponent);

        HttpExecuteRequest httpExecuteRequest = new HttpExecuteRequest(executionControl, scriptExecution, actionExecution);

        assertThrows(KeyValuePairException.class, httpExecuteRequest::prepare);
    }

    @Test
    void prepareStatusCode() throws Exception {

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
    void prepareMultipleStatusCode() throws Exception {

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


    ActionParameter createActionParameter(String parameterName, String value) {
        return ActionParameter.builder()
                .actionParameterKey(new ActionParameterKey(actionKey, parameterName))
                .value(value)
                .build();
    }

    Action createAction(ActionParameter... actionParameters) {
        return Action.builder()
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
                .getAndTrace("request", actionExecution, "request", "requestVersion");
    }

    HttpComponent createBaseComponent(List<HttpHeader> headers, List<HttpQueryParameter> queryParameters) {
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
                queryParameters);
    }

}
