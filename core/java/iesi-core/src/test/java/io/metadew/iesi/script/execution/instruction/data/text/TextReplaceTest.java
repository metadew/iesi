package io.metadew.iesi.script.execution.instruction.data.text;

import io.metadew.iesi.TestConfiguration;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.datatypes.text.Text;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TestConfiguration.class, DataTypeHandler.class })
@ActiveProfiles("test")
class TextReplaceTest {

    ExecutionRuntime executionRuntime;
    TextReplace textReplace;

    @SpyBean
    DataTypeHandler dataTypeHandlerSpy;

    @BeforeEach
    public void before() {
        executionRuntime = mock(ExecutionRuntime.class);
        textReplace = new TextReplace(executionRuntime);
    }

    @Test
    void textReplace() {
        doReturn(new Text(("\"source text\", \"t\", \"d\""))).when(dataTypeHandlerSpy).resolve("\"source text\", \"t\", \"d\"", executionRuntime);
        assertEquals("source dexd", textReplace.generateOutput("\"source text\", \"t\", \"d\""));
    }

    @Test
    void textReplaceTwo() {
        doReturn(new Text(("\"source text\", \"f\", \"d\""))).when(dataTypeHandlerSpy).resolve("\"source text\", \"f\", \"d\"", executionRuntime);
        assertEquals("source text", textReplace.generateOutput("\"source text\", \"f\", \"d\""));
    }

    @Test
    void textReplaceThree() {
        doReturn(new Text(("\"source text\", \" \""))).when(dataTypeHandlerSpy).resolve("\"source text\", \" \"", executionRuntime);
        assertEquals("sourcetext", textReplace.generateOutput("\"source text\", \" \""));
    }

    @Test
    void textReplaceFour() {
        doReturn(new Text(("\"source text\", \"t\""))).when(dataTypeHandlerSpy).resolve("\"source text\", \"t\"", executionRuntime);
        assertEquals("source ex", textReplace.generateOutput("\"source text\", \"t\""));
    }

    @Test
    void textReplaceThrowException() {
        doReturn(new Text(("Some String, For, Illegal, Exception"))).when(dataTypeHandlerSpy).resolve("Some String, For, Illegal, Exception", executionRuntime);
        assertThrows(IllegalArgumentException.class, () -> textReplace.generateOutput("Some String, For, Illegal, Exception"));
    }

    @Test
    void textReplaceWithSpecialCharacter() {
        doReturn(new Text(("\"+1234  +5678\", \"+\", \"0\""))).when(dataTypeHandlerSpy).resolve("\"+1234  +5678\", \"+\", \"0\"", executionRuntime);
        assertEquals("01234  05678", textReplace.generateOutput("\"+1234  +5678\", \"+\", \"0\""));
    }

    @Test
    void textReplaceByEmptySpace() {
        doReturn(new Text(("\"+12345678\", \"+\""))).when(dataTypeHandlerSpy).resolve("\"+12345678\", \"+\"", executionRuntime);
        assertEquals("12345678", textReplace.generateOutput("\"+12345678\", \"+\""));
    }

    @Test
    void textReplaceByEmptySpaceWithTwoArg() {
        doReturn(new Text(("\"+12+345+678\", \"+\""))).when(dataTypeHandlerSpy).resolve("\"+12+345+678\", \"+\"", executionRuntime);
        assertEquals("12345678", textReplace.generateOutput("\"+12+345+678\", \"+\""));
    }

    @Test
    void textReplaceByComma() {
        doReturn(new Text(("\"1234,567,8\", \",\", \".\""))).when(dataTypeHandlerSpy).resolve("\"1234,567,8\", \",\", \".\"", executionRuntime);
        assertEquals("1234.567.8", textReplace.generateOutput("\"1234,567,8\", \",\", \".\""));
    }

    @Test
    void textReplaceByQuote() {
        doReturn(new Text(("\"sour\"e text\", \"\"\", \"c\""))).when(dataTypeHandlerSpy).resolve("\"sour\"e text\", \"\"\", \"c\"", executionRuntime);
        assertEquals("source text" , textReplace.generateOutput("\"sour\"e text\", \"\"\", \"c\""));
    }

    @Test
    void textReplaceInstruction() {
        doReturn(new Text(("     \"source text\"    , \"t\",    \"c\"  "))).when(dataTypeHandlerSpy).resolve("     \"source text\"    , \"t\",    \"c\"  ", executionRuntime);
        assertEquals("source cexc" , textReplace.generateOutput("     \"source text\"    , \"t\",    \"c\"  "));
    }

    @Test
    void textReplaceBySpace() {
        doReturn(new Text(("\"         12345678\", \" \""))).when(dataTypeHandlerSpy).resolve("\"         12345678\", \" \"", executionRuntime);
        assertEquals("12345678", textReplace.generateOutput("\"         12345678\", \" \""));
    }
}
