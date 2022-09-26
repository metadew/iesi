package io.metadew.iesi.component.http;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.metadata.definition.component.ComponentParameter;
import io.metadew.iesi.metadata.definition.component.key.ComponentKey;
import io.metadew.iesi.metadata.definition.component.key.ComponentParameterKey;
import io.metadew.iesi.script.execution.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = { HttpQueryParameterService.class, DataTypeHandler.class})
@ContextConfiguration(classes = TestConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class HttpQueryParameterServiceTest {

    @Autowired
    private HttpQueryParameterService httpQueryParameterService;

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
                .thenReturn("value");
        when(executionRuntime.resolveVariables(actionExecution, "value"))
                .thenReturn("value");
        LookupResult lookupResult = new LookupResult();
        lookupResult.setValue("value");
        when(executionRuntime.resolveConceptLookup("value"))
                .thenReturn(lookupResult);
        when(executionRuntime.resolveVariables("value"))
                .thenReturn("value");

        assertThat(httpQueryParameterService.convert(new HttpQueryParameterDefinition("param", "function"), actionExecution))
                .isEqualTo(new HttpQueryParameter("param", "value"));
    }

}
