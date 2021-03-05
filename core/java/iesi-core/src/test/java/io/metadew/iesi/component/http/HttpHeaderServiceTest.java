package io.metadew.iesi.component.http;

import io.metadew.iesi.script.execution.*;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HttpHeaderServiceTest {


    @Test
    void convertTest() {
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
        when(actionRuntime.resolveRuntimeVariables("function"))
                .thenReturn("application/json");
        when(executionRuntime.resolveVariables(actionExecution, "application/json"))
                .thenReturn("application/json");
        LookupResult lookupResult = new LookupResult();
        lookupResult.setValue("application/json");
        when(executionRuntime.resolveConceptLookup("application/json"))
                .thenReturn(lookupResult);
        when(executionRuntime.resolveVariables("application/json"))
                .thenReturn("application/json");

        assertThat(HttpHeaderService.getInstance().convert(new HttpHeaderDefinition("content-type", "function"), actionExecution))
                .isEqualTo(new HttpHeader("content-type", "application/json"));
    }

}
