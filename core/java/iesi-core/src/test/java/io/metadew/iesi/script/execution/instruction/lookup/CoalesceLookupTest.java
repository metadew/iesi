package io.metadew.iesi.script.execution.instruction.lookup;

import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import io.metadew.iesi.script.execution.LookupResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.powermock.reflect.Whitebox;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CoalesceLookupTest {

    private DataTypeHandler dataTypeHandlerSpy;

    @BeforeEach
    void setup() {
        DataTypeHandler dataTypeHandler = DataTypeHandler.getInstance();
        dataTypeHandlerSpy = spy(dataTypeHandler);
        Whitebox.setInternalState(DataTypeHandler.class, "instance", dataTypeHandlerSpy);
    }

    @Test
    void test() {
        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);
        doReturn(new Text("test"))
                .when(dataTypeHandlerSpy)
                .resolve("test", executionRuntime);
        when(executionRuntime.resolveVariables("{{^null()}}"))
                .thenReturn("{{^null()}}");
        when(executionRuntime.resolveConceptLookup("{{^null()}}"))
                .thenReturn(new LookupResult("{{^null()}}", null, null));
        when(executionRuntime.resolveVariables("test"))
                .thenReturn("test");
        when(executionRuntime.resolveConceptLookup("test"))
                .thenReturn(new LookupResult("test", null, null));

        CoalesceLookup coalesceLookup = new CoalesceLookup(executionRuntime);
        assertThat(coalesceLookup.generateOutput("test"))
                .isEqualTo("test");
        assertThat(coalesceLookup.generateOutput(",test"))
                .isEqualTo("test");
        assertThat(coalesceLookup.generateOutput("{{^null()}},test"))
                .isEqualTo("test");


    }
}
