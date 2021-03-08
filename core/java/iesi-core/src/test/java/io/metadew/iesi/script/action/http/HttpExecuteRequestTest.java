package io.metadew.iesi.script.action.http;

import io.metadew.iesi.component.http.HttpComponent;
import io.metadew.iesi.component.http.HttpComponentService;
import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.connection.http.HttpConnection;
import io.metadew.iesi.connection.http.request.HttpGetRequest;
import io.metadew.iesi.connection.http.request.HttpRequestBuilderException;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.metadata.definition.action.Action;
import io.metadew.iesi.metadata.definition.action.ActionParameter;
import io.metadew.iesi.metadata.definition.action.key.ActionKey;
import io.metadew.iesi.metadata.definition.action.key.ActionParameterKey;
import io.metadew.iesi.metadata.definition.script.key.ScriptKey;
import io.metadew.iesi.script.execution.*;
import io.metadew.iesi.script.operation.ActionParameterOperation;
import io.metadew.iesi.script.service.ActionParameterService;
import org.apache.http.client.methods.HttpGet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

public class HttpExecuteRequestTest {

    ExecutionControl executionControl;
    ExecutionRuntime executionRuntime;
    ScriptExecution scriptExecution;
    ActionExecution actionExecution;
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
        when(executionControl.getExecutionRuntime())
                .thenReturn(executionRuntime);
        when(actionExecution.getActionControl())
                .thenReturn(actionControl);

        ActionParameterService actionParameterService = ActionParameterService.getInstance();
        actionParameterServiceSpy = Mockito.spy(actionParameterService);

        HttpComponentService httpComponentService = HttpComponentService.getInstance();
        httpComponentServiceSpy = Mockito.spy(httpComponentService);

        Whitebox.setInternalState(ActionParameterService.class, "instance", actionParameterServiceSpy);
        Whitebox.setInternalState(HttpComponentService.class, "instance", httpComponentServiceSpy);
    }

    @AfterEach
    void teardown() {
        Whitebox.setInternalState(ActionParameterService.class, "instance",  (ActionParameterService) null);
        Whitebox.setInternalState(HttpComponentService.class, "instance",  (HttpComponentService) null);
    }


    @Test
    void prepareTest() throws HttpRequestBuilderException, URISyntaxException {
        ActionKey actionKey = ActionKey.builder()
                .scriptKey(new ScriptKey("scriptId", 1L))
                .actionId("actionId")
                .build();
        ActionParameter requestActionParameter = ActionParameter.builder()
                .actionParameterKey(new ActionParameterKey(actionKey, "request"))
                .value("request")
                .build();
        Action action = Action.builder()
                .actionKey(actionKey)
                .errorExpected("N")
                .errorStop("Y")
                .retries("1")
                .number(0)
                .name("action1")
                .type("http.executeRequest")
                .parameters(Stream.of(
                        requestActionParameter
                ).collect(Collectors.toList()))
                .build();
        when(actionExecution.getAction())
                .thenReturn(action);
        doReturn(new Text("request"))
                .when(actionParameterServiceSpy)
                .getValue(requestActionParameter, executionRuntime, actionExecution);

        HttpComponent httpComponent = new HttpComponent("request",
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
                new ArrayList<>(),
                new ArrayList<>());
        doReturn(httpComponent)
                .when(httpComponentServiceSpy)
                .getAndTrace("request", actionExecution, "request");
        doReturn(new HttpGetRequest(new HttpGet("http://host/endpoint")))
                .when(httpComponentServiceSpy)
                .buildHttpRequest(httpComponent);

        HttpExecuteRequest httpExecuteRequest = new HttpExecuteRequest(executionControl, scriptExecution, actionExecution);
        httpExecuteRequest.prepare();

    }

}
