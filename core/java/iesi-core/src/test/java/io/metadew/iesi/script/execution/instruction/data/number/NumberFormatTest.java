package io.metadew.iesi.script.execution.instruction.data.number;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NumberFormatTest {

    @Test
    void numberFormat1() {
        NumberFormat numberFormat = new NumberFormat();
        assertEquals("1234567.89", numberFormat.generateOutput("1234567.89, \"*.**\""));
    }

    @Test
    void numberFormat2() {
        NumberFormat numberFormat = new NumberFormat();
        assertEquals("1234567.89", numberFormat.generateOutput("1234567.89, \"0.00\""));
    }


    @Test
    void numberFormat3() {
        NumberFormat numberFormat = new NumberFormat();
        assertEquals("1234567.89", numberFormat.generateOutput("1234567.89, \"*.***\""));
    }

    @Test
    void numberFormat4() {
        NumberFormat numberFormat = new NumberFormat();
        assertEquals("1234567.890", numberFormat.generateOutput("1234567.89, \"0.000\""));
    }


    @Test
    void numberFormat5() {
        NumberFormat numberFormat = new NumberFormat();
        assertEquals("1234567.8", numberFormat.generateOutput("1234567.89, \"*.*\""));
    }

    @Test
    void numberFormat6() {
        NumberFormat numberFormat = new NumberFormat();
        assertEquals("1234567.8", numberFormat.generateOutput("1234567.89, \"0.0\""));
    }


    @Test
    void numberFormat7() {
        NumberFormat numberFormat = new NumberFormat();
        assertEquals("1,234,567.89", numberFormat.generateOutput("1234567.89, \"*,***.**\""));
    }

    @Test
    void numberFormat8() {
        NumberFormat numberFormat = new NumberFormat();
        assertEquals("1,234,567", numberFormat.generateOutput("1234567.89, \"*,***\""));
    }

    @Test
    void numberFormat9() {
        NumberFormat numberFormat = new NumberFormat();
        assertEquals("1,234,567", numberFormat.generateOutput("\"1234567.89\", \"*,***\""));
    }

}
