package io.metadew.iesi.script.execution.instruction.lookup;

import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.script.execution.ExecutionRuntime;
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
        when(executionRuntime.resolveVariables(""))
                .thenReturn("");
        when(executionRuntime.resolveVariables("test"))
                .thenReturn("test");

        CoalesceLookup coalesceLookup = new CoalesceLookup(executionRuntime);
        assertThat(coalesceLookup.generateOutput("test"))
                .isEqualTo("test");
        assertThat(coalesceLookup.generateOutput(",test"))
                .isEqualTo("test");
        assertThat(coalesceLookup.generateOutput("{{^null()}},test"))
                .isEqualTo("test");
        assertThat(coalesceLookup.generateOutput("{{^null()}},{{^text()}}"))
                .isEmpty();
        assertThat(coalesceLookup.generateOutput("{{^null()}},{{^text()}},test"))
                .isEmpty();
        assertThat(coalesceLookup.generateOutput("{{^null()}},{{^text(test)}},{{^null()}}"))
                .isEqualTo("test");


    }
}
