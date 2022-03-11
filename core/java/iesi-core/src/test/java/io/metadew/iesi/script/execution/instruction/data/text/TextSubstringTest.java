package io.metadew.iesi.script.execution.instruction.data.text;

import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class TextSubstringTest {

    DataTypeHandler dataTypeHandler;
    DataTypeHandler dataTypeHandlerServiceSpy;
    ExecutionRuntime executionRuntime;
    TextSubstring textSubstring;

    @BeforeEach
    public void before() {
        dataTypeHandler = DataTypeHandler.getInstance();
        dataTypeHandlerServiceSpy = Mockito.spy(dataTypeHandler);
        Whitebox.setInternalState(DataTypeHandler.class, "instance", dataTypeHandlerServiceSpy);
        executionRuntime = mock(ExecutionRuntime.class);
        textSubstring = new TextSubstring(executionRuntime);
    }

    @AfterEach
    public void after() {
        Whitebox.setInternalState(DataTypeHandler.class, "instance", (DataTypeHandler) null);
    }

    @Test
    void substring() {
        doReturn(new Text(("teststring, 5, 8"))).when(dataTypeHandlerServiceSpy).resolve("teststring, 5, 8", executionRuntime);
        assertEquals("stri", textSubstring.generateOutput("teststring, 5, 8"));
    }

    @Test
    void substringEndMax() {
        doReturn(new Text(("teststring, 5, 10"))).when(dataTypeHandlerServiceSpy).resolve("teststring, 5, 10", executionRuntime);
        assertEquals("string", textSubstring.generateOutput("teststring, 5, 10"));
    }

    @Test
    void substringStartMin() {
        doReturn(new Text(("teststring, 1, 5"))).when(dataTypeHandlerServiceSpy).resolve("teststring, 1, 5", executionRuntime);
        assertEquals("tests", textSubstring.generateOutput("teststring, 1, 5"));
    }

    @Test
    void substringStartMinEndMax() {
        doReturn(new Text(("teststring, 1, 10"))).when(dataTypeHandlerServiceSpy).resolve("teststring, 1, 10", executionRuntime);
        assertEquals("teststring", textSubstring.generateOutput("teststring, 1, 10"));
    }

    @Test
    void substringNegativeArguments() {
        doReturn(new Text(("teststring, -8, -6"))).when(dataTypeHandlerServiceSpy).resolve("teststring, -8, -6", executionRuntime);
        assertEquals("sts", textSubstring.generateOutput("teststring, -8, -6"));
    }

    @Test
    void substringNegativeArgumentsStartMin() {
        doReturn(new Text(("teststring, -10, -6"))).when(dataTypeHandlerServiceSpy).resolve("teststring, -10, -6", executionRuntime);
        assertEquals("tests", textSubstring.generateOutput("teststring, -10, -6"));
    }

    @Test
    void substringTwoArguments() {
        doReturn(new Text(("teststring, 5"))).when(dataTypeHandlerServiceSpy).resolve("teststring, 5", executionRuntime);
        assertEquals("string", textSubstring.generateOutput("teststring, 5"));
    }

    @Test
    void substringTwoArgumentsStartMin() {
        doReturn(new Text(("teststring, 1"))).when(dataTypeHandlerServiceSpy).resolve("teststring, 1", executionRuntime);
        assertEquals("teststring", textSubstring.generateOutput("teststring, 1"));
    }

    @Test
    void substringTwoArgumentsNegativeArguments() {
        doReturn(new Text(("teststring, -8"))).when(dataTypeHandlerServiceSpy).resolve("teststring, -8", executionRuntime);
        assertEquals("ststring", textSubstring.generateOutput("teststring, -8"));
    }

    @Test
    void substringTextWithNewlines() {
        doReturn(new Text(("first line\n" +
                "second line\n" +
                "third line\n" +
                "fourth line\n" +
                "fifth line\n" +
                "sixth line\n" +
                "seventh line\n" +
                "eighth line\n, 11, 21"))).when(dataTypeHandlerServiceSpy).resolve("first line\n" +
                "second line\n" +
                "third line\n" +
                "fourth line\n" +
                "fifth line\n" +
                "sixth line\n" +
                "seventh line\n" +
                "eighth line\n, 11, 21", executionRuntime);
        assertEquals("second line", textSubstring.generateOutput("first line\n" +
                "second line\n" +
                "third line\n" +
                "fourth line\n" +
                "fifth line\n" +
                "sixth line\n" +
                "seventh line\n" +
                "eighth line\n, 11, 21"));
    }

    @Test
    void substringTextWithNewlinesCross() {
        doReturn(new Text(("first line\n" +
                "second line\n" +
                "third line\n" +
                "fourth line\n" +
                "fifth line\n" +
                "sixth line\n" +
                "seventh line\n" +
                "eighth line\n, 1, 31"))).when(dataTypeHandlerServiceSpy).resolve("first line\n" +
                "second line\n" +
                "third line\n" +
                "fourth line\n" +
                "fifth line\n" +
                "sixth line\n" +
                "seventh line\n" +
                "eighth line\n, 1, 31", executionRuntime);
        assertEquals(
                "first line\n" +
                        "second line\n" + "third line", textSubstring.generateOutput("first line\n" +
                        "second line\n" +
                        "third line\n" +
                        "fourth line\n" +
                        "fifth line\n" +
                        "sixth line\n" +
                        "seventh line\n" +
                        "eighth line\n, 1, 31"));
    }

    @Test
    void substringTextWithNewlinesMinValue() {
        doReturn(new Text(("first line\n" +
                "second line\n" +
                "third line\n" +
                "fourth line\n" +
                "fifth line\n" +
                "sixth line\n" +
                "seventh line\n" +
                "eighth line\n, 1, 10"))).when(dataTypeHandlerServiceSpy).resolve("first line\n" +
                "second line\n" +
                "third line\n" +
                "fourth line\n" +
                "fifth line\n" +
                "sixth line\n" +
                "seventh line\n" +
                "eighth line\n, 1, 10", executionRuntime);
        assertEquals("first line", textSubstring.generateOutput("first line\n" +
                "second line\n" +
                "third line\n" +
                "fourth line\n" +
                "fifth line\n" +
                "sixth line\n" +
                "seventh line\n" +
                "eighth line\n, 1, 10"));
    }

    @Test
    void substringTextWithNewlinesLastValue() {
        doReturn(new Text(("first line\n" +
                "second line\n" +
                "third line\n" +
                "fourth line\n" +
                "fifth line\n" +
                "sixth line\n" +
                "seventh line\n" +
                "eighth line\n, 75, 85"))).when(dataTypeHandlerServiceSpy).resolve("first line\n" +
                "second line\n" +
                "third line\n" +
                "fourth line\n" +
                "fifth line\n" +
                "sixth line\n" +
                "seventh line\n" +
                "eighth line\n, 75, 85", executionRuntime);
        assertEquals("eighth line", textSubstring.generateOutput("first line\n" +
                "second line\n" +
                "third line\n" +
                "fourth line\n" +
                "fifth line\n" +
                "sixth line\n" +
                "seventh line\n" +
                "eighth line\n, 75, 85"));
    }

    @Test
    void substringTextWithNewlinesTwoArguments() {
        doReturn(new Text(("first line\n" +
                "second line\n" +
                "third line\n" +
                "fourth line\n" +
                "fifth line\n" +
                "sixth line\n" +
                "seventh line\n" +
                "eighth line\n, 80"))).when(dataTypeHandlerServiceSpy).resolve("first line\n" +
                "second line\n" +
                "third line\n" +
                "fourth line\n" +
                "fifth line\n" +
                "sixth line\n" +
                "seventh line\n" +
                "eighth line\n, 80", executionRuntime);
        assertEquals("eighth line\n", textSubstring.generateOutput("first line\n" +
                "second line\n" +
                "third line\n" +
                "fourth line\n" +
                "fifth line\n" +
                "sixth line\n" +
                "seventh line\n" +
                "eighth line\n, 80"));
    }
}
