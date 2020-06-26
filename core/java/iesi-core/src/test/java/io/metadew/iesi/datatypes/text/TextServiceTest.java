package io.metadew.iesi.datatypes.text;

import com.fasterxml.jackson.databind.node.*;
import io.metadew.iesi.script.execution.ExecutionRuntime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
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


    @Test
    void equalsTest() {
        ExecutionRuntime executionRuntime = mock(ExecutionRuntime.class);
        assertThat(TextService.getInstance().equals(new Text(""), new Text(""), executionRuntime))
                .isTrue();
        assertThat(TextService.getInstance().equals(new Text("test"), new Text("test"), executionRuntime))
                .isTrue();
        assertThat(TextService.getInstance().equals(new Text(null), new Text(null), executionRuntime))
                .isTrue();
        assertThat(TextService.getInstance().equals(new Text("test1"), new Text("test2"), executionRuntime))
                .isFalse();
        assertThat(TextService.getInstance().equals(new Text("test1"), new Text("Test1"), executionRuntime))
                .isFalse();
        assertThat(TextService.getInstance().equals(new Text("test1"), new Text(null), executionRuntime))
                .isFalse();
        assertThat(TextService.getInstance().equals(new Text(null), new Text("Test1"), executionRuntime))
                .isFalse();
    }


}
