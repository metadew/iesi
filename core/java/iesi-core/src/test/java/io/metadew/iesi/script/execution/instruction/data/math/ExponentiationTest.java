package io.metadew.iesi.script.execution.instruction.data.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExponentiationTest {
    @Test
    void exponentiateDoubles() {
        Exponentiation exponentiation = new Exponentiation();
        assertEquals("25.0", exponentiation.generateOutput("5.0, 2.O"));
    }

    @Test
    void exponentiateInts() {
        Exponentiation exponentiation = new Exponentiation();
        assertEquals("25", exponentiation.generateOutput("5, 2"));
    }

    @Test
    void exponentiateIntWithDouble() {
        Exponentiation exponentiation = new Exponentiation();
        assertEquals("25.0", exponentiation.generateOutput("5, 2.0"));
    }

    @Test
    void illegalInput() {
        Exponentiation exponentiation = new Exponentiation();
        assertThrows(IllegalArgumentException.class, () -> exponentiation.generateOutput("illegal, 5.0"));
    }
}
