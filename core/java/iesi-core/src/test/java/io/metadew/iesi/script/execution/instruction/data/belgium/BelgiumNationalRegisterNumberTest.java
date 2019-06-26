package io.metadew.iesi.script.execution.instruction.data.belgium;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BelgiumNationalRegisterNumberTest {

    @Test
    void generateOutputPost2000Male() {
        BelgiumNationalRegisterNumber belgiumNationalRegisterNumber = new BelgiumNationalRegisterNumber();
        String belgiumNationalRegisterNumberOutput = belgiumNationalRegisterNumber.generateOutput("01051994, 1");
        assertEquals(11, belgiumNationalRegisterNumberOutput.length());
        assertEquals("940501", belgiumNationalRegisterNumberOutput.subSequence(0, 6));
        assertEquals(1, Integer.parseInt(belgiumNationalRegisterNumberOutput.subSequence(6, 9).toString()) % 2);
        assertEquals(97 - Integer.parseInt(belgiumNationalRegisterNumberOutput.subSequence(9, 11).toString()),
                Integer.parseInt(belgiumNationalRegisterNumberOutput.subSequence(0, 9).toString()) % 97);
    }

    @Test
    void generateOutputPost2000Female() {
        BelgiumNationalRegisterNumber belgiumNationalRegisterNumber = new BelgiumNationalRegisterNumber();
        String belgiumNationalRegisterNumberOutput = belgiumNationalRegisterNumber.generateOutput("01051994, 2");
        assertEquals(11, belgiumNationalRegisterNumberOutput.length());
        assertEquals("940501", belgiumNationalRegisterNumberOutput.subSequence(0, 6));
        assertEquals(0, Integer.parseInt(belgiumNationalRegisterNumberOutput.subSequence(6, 9).toString()) % 2);
        assertEquals(97 - Integer.parseInt(belgiumNationalRegisterNumberOutput.subSequence(9, 11).toString()),
                Integer.parseInt(belgiumNationalRegisterNumberOutput.subSequence(0, 9).toString()) % 97);
    }

    @Test
    void generateOutputPre2000Male() {
        BelgiumNationalRegisterNumber belgiumNationalRegisterNumber = new BelgiumNationalRegisterNumber();
        String belgiumNationalRegisterNumberOutput = belgiumNationalRegisterNumber.generateOutput("01052000, 1");
        assertEquals(11, belgiumNationalRegisterNumberOutput.length());
        assertEquals("000501", belgiumNationalRegisterNumberOutput.subSequence(0, 6));
        assertEquals(1, Integer.parseInt(belgiumNationalRegisterNumberOutput.subSequence(6, 9).toString()) % 2);
        assertEquals(Integer.parseInt(belgiumNationalRegisterNumberOutput.subSequence(9, 11).toString()),
                97 - (2 * (int) Math.pow(10, 9) + Integer.parseInt(belgiumNationalRegisterNumberOutput.subSequence(0, 9).toString()))
                        % 97);
    }

    @Test
    void generateOutputPre2000Female() {
        BelgiumNationalRegisterNumber belgiumNationalRegisterNumber = new BelgiumNationalRegisterNumber();
        String belgiumNationalRegisterNumberOutput = belgiumNationalRegisterNumber.generateOutput("01052000, 2");
        assertEquals(11, belgiumNationalRegisterNumberOutput.length());
        assertEquals("000501", belgiumNationalRegisterNumberOutput.subSequence(0, 6));
        assertEquals(0, Integer.parseInt(belgiumNationalRegisterNumberOutput.subSequence(6, 9).toString()) % 2);
        assertEquals(Integer.parseInt(belgiumNationalRegisterNumberOutput.subSequence(9, 11).toString()),
                97 - (2 * (int) Math.pow(10, 9) + Integer.parseInt(belgiumNationalRegisterNumberOutput.subSequence(0, 9).toString()))
                        % 97);
    }

    @Test
    void generateOutputInputErrorSex() {
        BelgiumNationalRegisterNumber belgiumNationalRegisterNumber = new BelgiumNationalRegisterNumber();
        assertThrows(IllegalArgumentException.class, () -> belgiumNationalRegisterNumber.generateOutput("test, a"));
    }

    @Test
    void generateOutputInputErrorUnknownSex() {
        BelgiumNationalRegisterNumber belgiumNationalRegisterNumber = new BelgiumNationalRegisterNumber();
        assertThrows(IllegalArgumentException.class, () -> belgiumNationalRegisterNumber.generateOutput("01052000, 3"));
    }

    @Test
    void generateOutputInputErrorDateformat() {
        BelgiumNationalRegisterNumber belgiumNationalRegisterNumber = new BelgiumNationalRegisterNumber();
        assertThrows(IllegalArgumentException.class, () -> belgiumNationalRegisterNumber.generateOutput("301052000, 3"));
    }
}
