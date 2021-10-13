package io.metadew.iesi.script.execution.instruction.data.text;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TextFormatTest {

    @Test
    void textFormat6() {
        TextFormat textFormat = new TextFormat();
        assertEquals("Hello", textFormat.generateOutput("Hello, %s"));
    }

    @Test
    void textFormat7() {
        TextFormat textFormat = new TextFormat();
        assertEquals(" Hello World", textFormat.generateOutput("Hello World, %12s"));
    }

    @Test
    void textFormat8() {
        TextFormat textFormat = new TextFormat();
        assertEquals("Hello World ", textFormat.generateOutput("Hello World, %-12s"));
    }

    @Test
    void textFormat9() {
        TextFormat textFormat = new TextFormat();
        assertEquals("Hell", textFormat.generateOutput("Hello World, %.4s"));
    }

    @Test
    void textFormat10() {
        TextFormat textFormat = new TextFormat();
        assertEquals("  Hello Worl", textFormat.generateOutput("Hello World, %12.10s"));
    }


}
