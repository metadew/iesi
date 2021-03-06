package io.metadew.iesi.script.execution.instruction.data.text;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TextSubstringTest {

    @Test
    void substring() {
        TextSubstring textSubstring = new TextSubstring();
        assertEquals("stri", textSubstring.generateOutput("teststring, 5, 8"));
    }

    @Test
    void substringEndMax() {
        TextSubstring textSubstring = new TextSubstring();
        assertEquals("string", textSubstring.generateOutput("teststring, 5, 10"));
    }

    @Test
    void substringStartMin() {
        TextSubstring textSubstring = new TextSubstring();
        assertEquals("tests", textSubstring.generateOutput("teststring, 1, 5"));
    }

    @Test
    void substringStartMinEndMax() {
        TextSubstring textSubstring = new TextSubstring();
        assertEquals("teststring", textSubstring.generateOutput("teststring, 1, 10"));
    }

    @Test
    void substringNegativeArguments() {
        TextSubstring textSubstring = new TextSubstring();
        assertEquals("sts", textSubstring.generateOutput("teststring, -8, -6"));
    }

    @Test
    void substringNegativeArgumentsStartMin() {
        TextSubstring textSubstring = new TextSubstring();
        assertEquals("tests", textSubstring.generateOutput("teststring, -10, -6"));
    }

    @Test
    void substringTwoArguments() {
        TextSubstring textSubstring = new TextSubstring();
        assertEquals("string", textSubstring.generateOutput("teststring, 5"));
    }

    @Test
    void substringTwoArgumentsStartMin() {
        TextSubstring textSubstring = new TextSubstring();
        assertEquals("teststring", textSubstring.generateOutput("teststring, 1"));
    }

    @Test
    void substringTwoArgumentsNegativeArguments() {
        TextSubstring textSubstring = new TextSubstring();
        assertEquals("ststring", textSubstring.generateOutput("teststring, -8"));
    }

    @Test
    void substringTextWithNewlines() {
        TextSubstring textSubstring = new TextSubstring();
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
        TextSubstring textSubstring = new TextSubstring();
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
        TextSubstring textSubstring = new TextSubstring();
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
        TextSubstring textSubstring = new TextSubstring();
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
        TextSubstring textSubstring = new TextSubstring();
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
