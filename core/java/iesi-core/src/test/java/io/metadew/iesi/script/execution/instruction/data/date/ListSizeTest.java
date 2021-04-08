package io.metadew.iesi.script.execution.instruction.data.date;

import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes._null.Null;
import io.metadew.iesi.datatypes.array.Array;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ListSizeTest {

    DataTypeHandler dataTypeHandler;
    DataTypeHandler dataTypeHandlerServiceSpy;
    ExecutionRuntime executionRuntime;
    ListSize listSize;


    @BeforeEach
    public void before() {
        dataTypeHandler = DataTypeHandler.getInstance();
        dataTypeHandlerServiceSpy = Mockito.spy(dataTypeHandler);
        Whitebox.setInternalState(DataTypeHandler.class, "instance", dataTypeHandlerServiceSpy);
        executionRuntime = mock(ExecutionRuntime.class);
        listSize = new ListSize(executionRuntime);
    }

    @AfterEach
    public void after() {
        Whitebox.setInternalState(DataTypeHandler.class, "instance", (DataTypeHandler) null);
    }

    @Test
    void getKeyword() {
        listSize = new ListSize(executionRuntime);
        assertThat(listSize.getKeyword()).isEqualTo("list.size");
    }

    @Test
    void generateOutputWithText() {
        doReturn(new Text("array")).when(dataTypeHandlerServiceSpy).resolve("array", executionRuntime);
        when(executionRuntime.getArray("array")).thenReturn(Optional.of(new Array(new ArrayList<>())));
        ListSize listSize = new ListSize(executionRuntime);
        assertThat(listSize.generateOutput("array")).isEqualTo("0");
    }

    @Test
    void generateOutputWithOptionalArray() {
        doReturn(new Text("array")).when(dataTypeHandlerServiceSpy).resolve("array", executionRuntime);
        when(executionRuntime.getArray("array")).thenReturn(Optional.empty());
        ListSize listSize = new ListSize(executionRuntime);
        assertThrows(IllegalArgumentException.class, () -> listSize.generateOutput("array"));
    }

    @Test
    void generateOutputWithList() {
        doReturn(new Array(Stream.of(new Text("1"), new Text("2"), new Text("3")).collect(Collectors.toList())))
                .when(dataTypeHandlerServiceSpy).resolve("{{^list(1,2,3)}}", executionRuntime);
        listSize = new ListSize(executionRuntime);
        assertThat(listSize.generateOutput("{{^list(1,2,3)}}")).isEqualTo("3");
    }

    @Test
    void generateOutputWithNull() {
        doReturn(new Null()).when(dataTypeHandlerServiceSpy).resolve(null, executionRuntime);
        listSize = new ListSize(executionRuntime);
        assertThrows(IllegalArgumentException.class, () -> listSize.generateOutput(null));

    }
}
