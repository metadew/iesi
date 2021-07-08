package io.metadew.iesi.script.execution.instruction.data.text;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TextReplaceTest {

    @Test
    void textReplace() {
        TextReplace textReplace = new TextReplace();
        assertEquals("source dexd", textReplace.generateOutput("\"source text\",\"t\",\"d\""));
    }

    @Test
    void textReplaceTwo() {
        TextReplace textReplace = new TextReplace();
        assertEquals("source text", textReplace.generateOutput("\"source text\",\"f\",\"d\""));
    }

    @Test
    void textReplaceThree() {
        TextReplace textReplace = new TextReplace();
        assertEquals("sourcetext", textReplace.generateOutput("\"source text\",\" \""));
    }

    @Test
    void textReplaceFour() {
        TextReplace textReplace = new TextReplace();
        assertEquals("source ex", textReplace.generateOutput("\"source text\",\"t\""));
    }

    @Test
    void textReplaceThrowException() {
        TextReplace textReplace = new TextReplace();
        assertThrows(IllegalArgumentException.class, () -> textReplace.generateOutput("Some String, For, Illegal, Exception"));
    }

    @Test
    void textReplaceWithSpecialCharacter() {
        TextReplace textReplace = new TextReplace();
        assertEquals("01234  05678", textReplace.generateOutput("\"+1234  +5678\",\"+\",\"0\""));
    }

    @Test
    void textReplaceByEmptySpace() {
        TextReplace textReplace = new TextReplace();
        assertEquals("12345678", textReplace.generateOutput("\"+12345678\",\"+\""));
    }

    @Test
    void textReplaceByEmptySpaceWithTwoArg() {
        TextReplace textReplace = new TextReplace();
        assertEquals("12345678", textReplace.generateOutput("\"+12+345+678\",\"+\""));
    }

    @Test
    void textReplaceByComma() {
        TextReplace textReplace = new TextReplace();
        assertEquals("1234.567.8", textReplace.generateOutput("\"1234,567,8\",\",\",\".\""));
    }

}
