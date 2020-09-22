package io.metadew.iesi.connection.http;

import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HttpConnectionTraceServiceTest {

    @Test
    void test() {
        ActionExecution actionExecution = mock(ActionExecution.class);
        ExecutionControl executionControl = mock(ExecutionControl.class);
        when(actionExecution.getExecutionControl())
                .thenReturn(executionControl);
        when(actionExecution.getProcessId())
                .thenReturn(1L);
        when(executionControl.getRunId())
                .thenReturn("runId");
//        HttpConnectionTraceService.getInstance().trace(
//                new HttpConnection("referenceName", "description", "environment", "host", "baseurl", 8080, true),
//                actionExecution,
//                "test"
//        );
        //ConnectionTraceConfiguration.getInstance().insert(HttpConnectionTrace.builder().build());
    }

}
