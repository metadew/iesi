package io.metadew.iesi.script.execution.instruction.data.date;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DateFormatTest {

    @Test
    void generateOutputddMMyyyy() {
        DateFormat dateFormatddMMyyyy = new DateFormat();
        assertEquals("01052000", dateFormatddMMyyyy.generateOutput("01052000, \"ddMMyyyy\""));
    }

    @Test
    void generateOutputHyphen() {
        DateFormat dateFormatddMMyyyy = new DateFormat();
        assertEquals("01-05-2000", dateFormatddMMyyyy.generateOutput("01052000, \"dd-MM-yyyy\""));
    }

    @Test
    void generateOutputSlash() {
        DateFormat dateFormatddMMyyyy = new DateFormat();
        assertEquals("01/05/2000", dateFormatddMMyyyy.generateOutput("01052000, \"dd/MM/yyyy\""));
    }

    @Test
    void generateOutputText() {
        DateFormat dateFormatddMMyyyy = new DateFormat();
        assertEquals("01 of 05 of 2000", dateFormatddMMyyyy.generateOutput("01052000, \"dd 'of' MM 'of' yyyy\""));
    }

    @Test
    void generateOutputMMddyyyy() {
        DateFormat dateFormatddMMyyyy = new DateFormat();
        assertEquals("05012000", dateFormatddMMyyyy.generateOutput("01052000, \"MMddyyyy\""));
    }

    @Test
    void generateOutputParseError() {
        DateFormat dateFormatddMMyyyy = new DateFormat();
        assertThrows(IllegalArgumentException.class, () -> dateFormatddMMyyyy.generateOutput("01052000 11:11:11, \"xxyyzzzz\""));
    }

    @Test
    void generateOutputInputError() {
        DateFormat dateFormatddMMyyyy = new DateFormat();
        assertThrows(IllegalArgumentException.class, () -> dateFormatddMMyyyy.generateOutput("01052000, \"xxyyzzzz\""));
    }
}
