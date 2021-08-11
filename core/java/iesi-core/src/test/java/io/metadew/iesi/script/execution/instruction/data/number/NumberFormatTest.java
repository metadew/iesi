package io.metadew.iesi.script.execution.instruction.data.number;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NumberFormatTest {

    @Test
    void numberFormat() {
        NumberFormat numberFormat = new NumberFormat();
        assertEquals("|00000000000000000093|", numberFormat.generateOutput("\"93\", \"|%020d|\""));
    }

    @Test
    void numberFormatWithWidth() {
        NumberFormat numberFormat = new NumberFormat();
        assertEquals("|                  93|", numberFormat.generateOutput("\"93\", \"|%20d|\""));
    }

    @Test
    void numberFormatWithLeftJustifyingWidth() {
        NumberFormat numberFormat = new NumberFormat();
        assertEquals("|93                  |", numberFormat.generateOutput("\"93\", \"|%-20d|\""));
    }

    @Test
    void numberFormatWithComma() {
        NumberFormat numberFormat = new NumberFormat();
        assertEquals("|10,000,000|", numberFormat.generateOutput("\"10000000\", \"|%,d|\""));
    }

    @Test
    void numberFormatWithNegative() {
        NumberFormat numberFormat = new NumberFormat();
        assertEquals("|(25)|", numberFormat.generateOutput("\"-25\", \"|%(d|\""));
    }

    @Test
    void numberFormatThrowNumberFormat() {
        NumberFormat numberFormat = new NumberFormat();
        assertThrows(NumberFormatException.class, () -> numberFormat.generateOutput("\"25a\", \"|%d|\""));
    }

    @Test
    void numberFormatThrowIllegalArgument() {
        NumberFormat numberFormat = new NumberFormat();
        assertThrows(IllegalArgumentException.class, () -> numberFormat.generateOutput("25a, |%d|"));
    }

    @Test
    void numberFormatWithoutMod() {
        NumberFormat numberFormat = new NumberFormat();
        assertEquals("00000000000000000093", numberFormat.generateOutput("\"93\", \"%020d\""));
    }

    @Test
    void numberFormatWithoutDoubleQuotes() {
        NumberFormat numberFormat = new NumberFormat();
        assertEquals("00000000000000000093", numberFormat.generateOutput("93, \"%020d\""));
    }

}
