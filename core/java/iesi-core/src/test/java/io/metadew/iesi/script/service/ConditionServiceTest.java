package io.metadew.iesi.script.service;

import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionControl;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import io.metadew.iesi.script.execution.LookupResult;
import org.junit.jupiter.api.Test;

import javax.script.ScriptException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

// https://commons.apache.org/proper/commons-jexl/reference/syntax.html
class ConditionServiceTest {

    @Test
    void testSimpleJexlExpressions() throws ScriptException {
        // Mocking
        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);
        ActionExecution actionExecution = mock(ActionExecution.class);

        String condition1 = "true";
        when(executionRuntime.resolveVariables(actionExecution, condition1))
                .thenReturn(condition1);
        when(executionRuntime.resolveConceptLookup(condition1))
                .thenReturn(new LookupResult(condition1, "", condition1));
        String condition2 = "\"a\" == \"a\"";
        when(executionRuntime.resolveVariables(actionExecution, condition2))
                .thenReturn(condition2);
        when(executionRuntime.resolveConceptLookup(condition2))
                .thenReturn(new LookupResult(condition2, "", condition2));
        String condition3 = "\"a\" != \"a\"";
        when(executionRuntime.resolveVariables(actionExecution, condition3))
                .thenReturn(condition3);
        when(executionRuntime.resolveConceptLookup(condition3))
                .thenReturn(new LookupResult(condition3, "", condition2));

        assertThat(ConditionService.getInstance().evaluateCondition(condition1, executionRuntime, actionExecution))
                .isTrue();
        assertThat(ConditionService.getInstance().evaluateCondition(condition2, executionRuntime, actionExecution))
                .isTrue();
        assertThat(ConditionService.getInstance().evaluateCondition(condition3, executionRuntime, actionExecution))
                .isFalse();
    }
    @Test
    void testResolvedJexlExpressions() throws ScriptException {
        // Mocking
        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);
        ActionExecution actionExecution = mock(ActionExecution.class);

        String condition1 = "#trueVariable#";
        when(executionRuntime.resolveVariables(actionExecution, condition1))
                .thenReturn("true");
        when(executionRuntime.resolveConceptLookup("true"))
                .thenReturn(new LookupResult("true", "", "true"));
        String condition2 = "\"#stringVariable#\" == \"a\"";
        when(executionRuntime.resolveVariables(actionExecution, condition2))
                .thenReturn("\"a\" == \"a\"");
        when(executionRuntime.resolveConceptLookup("\"a\" == \"a\""))
                .thenReturn(new LookupResult("\"a\" == \"a\"", "", "\"a\" == \"a\""));
        String condition3 = "\"#stringVariable2#\" == \"a\"";
        when(executionRuntime.resolveVariables(actionExecution, condition3))
                .thenReturn("\"b\" == \"a\"");
        when(executionRuntime.resolveConceptLookup("\"b\" == \"a\""))
                .thenReturn(new LookupResult("\"b\" == \"a\"", "", "\"b\" == \"a\""));

        assertThat(ConditionService.getInstance().evaluateCondition(condition1, executionRuntime, actionExecution))
                .isTrue();
        assertThat(ConditionService.getInstance().evaluateCondition(condition2, executionRuntime, actionExecution))
                .isTrue();
        assertThat(ConditionService.getInstance().evaluateCondition(condition3, executionRuntime, actionExecution))
                .isFalse();
    }


    @Test
    void test() {
        ExecutionControl executionControl = mock(ExecutionControl.class);
        ExecutionRuntime executionRuntime = new ExecutionRuntime(executionControl, "runId");
        System.out.println(executionRuntime.resolveConceptLookup("{{*date.today()}} == {{*date.today()}}").getValue());
    }

}
