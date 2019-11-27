package io.metadew.iesi.script.execution.instruction.data.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DivisionTest {
    @Test
    void divideDoubles() {
        Division division = new Division();
        assertEquals("3.5", division.generateOutput("7.0, 2.0"));
    }

    @Test
    void divideInts() {
        Division division = new Division();
        assertEquals("3.5", division.generateOutput("7, 2"));
    }

    @Test
    void divideDoubleWithInt() {
        Division division = new Division();
        assertEquals("3.5", division.generateOutput("7.0, 2"));
    }

    @Test
    void illegalInput() {
        Division division = new Division();
        assertThrows(IllegalArgumentException.class, () -> division.generateOutput("illegal, 5.0"));
    }
}
