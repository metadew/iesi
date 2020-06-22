package io.metadew.iesi.script.execution.instruction.data.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MultiplicationTest {
    @Test
    void multiplyDoubles() {
        Multiplication multiplication = new Multiplication();
        assertEquals("10.0", multiplication.generateOutput("2.0, 5.0"));
    }

    @Test
    void multiplyInts() {
        Multiplication multiplication = new Multiplication();
        assertEquals("10", multiplication.generateOutput("2, 5"));
    }

    @Test
    void multiplyIntWithDouble() {
        Multiplication multiplication = new Multiplication();
        assertEquals("10.0", multiplication.generateOutput("2, 5.0"));
    }

    @Test
    void illegalInput() {
        Multiplication multiplication = new Multiplication();
        assertThrows(IllegalArgumentException.class, () -> multiplication.generateOutput("illegal, 5.0"));
    }
}
