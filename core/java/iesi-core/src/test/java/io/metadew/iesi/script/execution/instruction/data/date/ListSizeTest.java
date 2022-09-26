package io.metadew.iesi.script.execution.instruction.data.date;

import io.metadew.iesi.TestConfiguration;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = DataTypeHandler.class )
@ContextConfiguration(classes = TestConfiguration.class)
@DirtiesContext
@ActiveProfiles("test")
class ListSizeTest {

    ExecutionRuntime executionRuntime;
    ListSize listSize;

    @SpyBean
    DataTypeHandler dataTypeHandlerSpy;


    @BeforeEach
    public void before() {
        executionRuntime = mock(ExecutionRuntime.class);
        listSize = new ListSize(executionRuntime);
    }

    @Test
    void getKeyword() {
        assertThat(listSize.getKeyword()).isEqualTo("list.size");
    }

    @Test
    void generateOutputWithText() {
        doReturn(new Text("array")).when(dataTypeHandlerSpy).resolve("array", executionRuntime);
        when(executionRuntime.getArray("array")).thenReturn(Optional.of(new Array(new ArrayList<>())));
        assertThat(listSize.generateOutput("array")).isEqualTo("0");
    }

    @Test
    void generateOutputWithOptionalArray() {
        doReturn(new Text("array")).when(dataTypeHandlerSpy).resolve("array", executionRuntime);
        when(executionRuntime.getArray("array")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> listSize.generateOutput("array"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No array array found in memory");
    }

    @Test
    void generateOutputWithList() {
        doReturn(new Array(Stream.of(new Text("1"), new Text("2"), new Text("3")).collect(Collectors.toList())))
                .when(dataTypeHandlerSpy).resolve("{{^list(1,2,3)}}", executionRuntime);
        assertThat(listSize.generateOutput("{{^list(1,2,3)}}")).isEqualTo("3");
    }

    @Test
    void generateOutputWithNull() {
        doReturn(new Null()).when(dataTypeHandlerSpy).resolve(null, executionRuntime);
        assertThatThrownBy(() -> listSize.generateOutput(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("list cannot be of type class io.metadew.iesi.datatypes._null.Null");
    }

}
