package io.metadew.iesi.script.execution.instruction.data.text;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TextReplaceTest {

    @Test
    void textReplace() {
        TextReplace textReplace = new TextReplace();
        assertEquals("source dexd", textReplace.generateOutput("source text,t,d"));
    }

    @Test
    void textReplaceTwo() {
        TextReplace textReplace = new TextReplace();
        assertEquals("source text", textReplace.generateOutput("source text,f,d"));
    }

    @Test
    void textReplaceThree() {
        TextReplace textReplace = new TextReplace();
        assertEquals("sourcetext", textReplace.generateOutput("source text, ,"));
    }

    @Test
    void textReplaceFour() {
        TextReplace textReplace = new TextReplace();
        assertEquals("source ex", textReplace.generateOutput("source text,t,"));
    }

    @Test
    void textReplaceThrowException() {
        TextReplace textReplace = new TextReplace();
        assertThrows(IllegalArgumentException.class, () -> textReplace.generateOutput("source text,,"));
    }

}
