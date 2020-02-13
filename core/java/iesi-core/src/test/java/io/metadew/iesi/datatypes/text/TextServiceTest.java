package io.metadew.iesi.datatypes.text;

import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.node.ValueNode;
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

    void resolveTestValueNode() {
        new TextNode("testing");
        assertEquals(new Text("testing"), null);
    }

    public Text resolve(NullNode jsonNode) {
        return new Text("");
    }


}
