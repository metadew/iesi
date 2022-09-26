package io.metadew.iesi.script.service;

import io.metadew.iesi.script.execution.ActionExecution;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import io.metadew.iesi.script.execution.LookupResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.script.ScriptException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ConditionService.class )
@ActiveProfiles("test")
class ConditionServiceTest {

    @Autowired
    private ConditionService conditionService;

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

        assertThat(conditionService.evaluateCondition(condition1, executionRuntime, actionExecution))
                .isTrue();
        assertThat(conditionService.evaluateCondition(condition2, executionRuntime, actionExecution))
                .isTrue();
        assertThat(conditionService.evaluateCondition(condition3, executionRuntime, actionExecution))
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

        assertThat(conditionService.evaluateCondition(condition1, executionRuntime, actionExecution))
                .isTrue();
        assertThat(conditionService.evaluateCondition(condition2, executionRuntime, actionExecution))
                .isTrue();
        assertThat(conditionService.evaluateCondition(condition3, executionRuntime, actionExecution))
                .isFalse();
    }

}
