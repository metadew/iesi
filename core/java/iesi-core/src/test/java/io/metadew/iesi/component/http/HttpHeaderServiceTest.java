package io.metadew.iesi.component.http;

import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentParameterKey;
import io.metadew.iesi.script.execution.*;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HttpHeaderServiceTest {

    @Test
    void isHeaderTrueTest() {
        assertThat(HttpHeaderService.getInstance().isHeader(ComponentParameter.builder()
                .componentParameterKey(new ComponentParameterKey(new ComponentKey("id", 1L), "header.1"))
                .value("content-type, application/json")
                .build())
        ).isTrue();
    }

    @Test
    void isHeaderFalseTest() {
        assertThat(HttpHeaderService.getInstance().isHeader(ComponentParameter.builder()
                .componentParameterKey(new ComponentParameterKey(new ComponentKey("id", 1L), "notheader.1"))
                .value("content-type, application/json")
                .build())
        ).isFalse();
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
        when(executionControl.getExecutionRuntime())
                .thenReturn(executionRuntime);
        when(actionExecution.getActionControl())
                .thenReturn(actionControl);
        when(actionControl.getActionRuntime())
                .thenReturn(actionRuntime);
        when(actionRuntime.resolveRuntimeVariables("application/json"))
                .thenReturn("application/json");
        when(executionRuntime.resolveVariables(actionExecution, "application/json"))
                .thenReturn("application/json");
        LookupResult lookupResult = new LookupResult();
        lookupResult.setValue("application/json");
        when(executionRuntime.resolveConceptLookup("application/json"))
                .thenReturn(lookupResult);
        when(executionRuntime.resolveVariables("application/json"))
                .thenReturn("application/json");

        assertThat(HttpHeaderService.getInstance().convert(ComponentParameter.builder()
                .componentParameterKey(new ComponentParameterKey(new ComponentKey("id", 1L), "header.1"))
                .value("content-type,application/json")
                .build(), actionExecution)
        ).isEqualTo(new HttpHeader("content-type", "application/json"));
    }

}
