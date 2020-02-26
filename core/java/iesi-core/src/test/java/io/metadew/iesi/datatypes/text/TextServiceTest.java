package io.metadew.iesi.datatypes.text;

import com.fasterxml.jackson.databind.node.*;
import io.metadew.iesi.datatypes.DataTypeHandler;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TextServiceTest {

    @Test
    void appliesToTest() {
        assertEquals(Text.class, TextService.getInstance().appliesTo());
    }


    @Test
    void keywordTest() {
        assertEquals("text", TextService.getInstance().keyword());
    }

    @Test
    void resolveInputTest() {
        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);
        when(executionRuntime.resolveVariables(anyString())).thenReturn("testing");

        assertEquals(new Text("testing"), TextService.getInstance().resolve("testing", executionRuntime));
    }

    @Test
    void resolveValueNodeTest() {
        assertEquals(new Text("testing"), TextService.getInstance().resolve(new TextNode("testing")));
        assertEquals(new Text("true"), TextService.getInstance().resolve(BooleanNode.getTrue()));
        assertEquals(new Text("1.0"), TextService.getInstance().resolve(new DoubleNode(1.0)));
        assertEquals(new Text("1"), TextService.getInstance().resolve(new IntNode(1)));
    }

    @Test
    void resolveNullNodeTest() {
        assertEquals(new Text(""), TextService.getInstance().resolve(NullNode.getInstance()));
    }


}
