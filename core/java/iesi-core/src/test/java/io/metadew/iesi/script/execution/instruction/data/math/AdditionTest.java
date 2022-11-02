package io.metadew.iesi.script.execution.instruction.data.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AdditionTest {
    @Test
    void addDoubles() {
        Addition addition = new Addition();
        assertEquals("10.0", addition.generateOutput("5.0, 5.0"));
    }

    @Test
    void addInts() {
        Addition addition = new Addition();
        assertEquals("10", addition.generateOutput("5, 5"));
    }

    @Test
    void addIntWithDouble() {
        Addition addition = new Addition();
        assertEquals("10.0", addition.generateOutput("5, 5.0"));
    }

    @Test
    void illegalInput() {
        Addition addition = new Addition();
        assertThrows(IllegalArgumentException.class, () -> addition.generateOutput("illegal, 5.0"));
    }

    @Test
    void emptyInput(){
        Addition addition = new Addition();
        assertThrows(IllegalArgumentException.class, () -> addition.generateOutput(""));
    }
}
