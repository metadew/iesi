package io.metadew.iesi.script.execution.instruction.data.text;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TextSubstringTest {

    @Test
    void substring() {
        TextSubstring textSubstring = new TextSubstring();
        assertEquals("tri", textSubstring.generateOutput("teststring, 5, 8"));
    }

    @Test
    void subtringEndMax() {
        TextSubstring textSubstring = new TextSubstring();
        assertEquals("tring", textSubstring.generateOutput("teststring, 5, 10"));
    }

    @Test
    void subtringStartMin() {
        TextSubstring textSubstring = new TextSubstring();
        assertEquals("tests", textSubstring.generateOutput("teststring, 0, 5"));
    }

    @Test
    void subtringStartMinEndMax() {
        TextSubstring textSubstring = new TextSubstring();
        assertEquals("teststring", textSubstring.generateOutput("teststring, 0, 10"));
    }

    @Test
    void substringNegativeArguments() {
        TextSubstring textSubstring = new TextSubstring();
        assertEquals("sts", textSubstring.generateOutput("teststring, -8, -5"));
    }

    @Test
    void substringNegativeArgumentsStartMin() {
        TextSubstring textSubstring = new TextSubstring();
        assertEquals("tests", textSubstring.generateOutput("teststring, -10, -5"));
    }
}
