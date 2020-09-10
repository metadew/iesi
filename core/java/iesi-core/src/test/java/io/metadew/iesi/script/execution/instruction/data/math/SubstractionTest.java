package io.metadew.iesi.script.execution.instruction.data.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SubstractionTest {
    @Test
    void substractDoubles() {
        Substraction substraction = new Substraction();
        assertEquals("10.0", substraction.generateOutput("15.0, 5.0"));
    }

    @Test
    void substractInts() {
        Substraction substraction = new Substraction();
        assertEquals("10", substraction.generateOutput("15, 5"));
    }

    @Test
    void substractIntWithDouble() {
        Substraction substraction = new Substraction();
        assertEquals("10.0", substraction.generateOutput("15, 5.0"));
    }

    @Test
    void illegalInput() {
        Substraction substraction = new Substraction();
        assertThrows(IllegalArgumentException.class, () -> substraction.generateOutput("illegal, 5.0"));
    }
}
