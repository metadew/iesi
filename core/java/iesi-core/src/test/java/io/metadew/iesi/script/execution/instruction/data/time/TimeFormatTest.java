package io.metadew.iesi.script.execution.instruction.data.time;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TimeFormatTest {
    // yyyy-MM-dd HH:mm:ss.SSS

    @Test
    void generateOutputHHmmssSSS() {
        TimeFormat timeFormat = new TimeFormat();
        assertEquals("2000-05-02 12:12:12.121", timeFormat.generateOutput("2000-05-02 12:12:12.121, \"yyyy-MM-dd HH:mm:ss.SSS\""));
    }

    @Test
    void generateOutputHHmmss() {
        TimeFormat timeFormat = new TimeFormat();
        assertEquals("05-02-2000 12:12:12", timeFormat.generateOutput("2000-05-02 12:12:12.121, \"MM-dd-yyyy HH:mm:ss\""));
    }

    @Test
    void generateOutputSlash() {
        TimeFormat timeFormat = new TimeFormat();
        assertEquals("05/02/2000 12:12:12", timeFormat.generateOutput("2000-05-02 12:12:12.121, \"MM/dd/yyyy HH:mm:ss\""));
    }

    @Test
    void generateOutputText() {
        TimeFormat timeFormat = new TimeFormat();
        assertEquals("05 of 02 of 2000 12:12:12",
                timeFormat.generateOutput("2000-05-02 12:12:12.121, \"MM 'of' dd 'of' yyyy HH:mm:ss\""));
    }

    @Test
    void generateOutputParseError() {
        TimeFormat timeFormat = new TimeFormat();
        assertThrows(IllegalArgumentException.class, () -> timeFormat.generateOutput("abc, \"MM-dd-yyyy HH:mm:ss\""));
    }

    @Test
    void generateOutputInputError() {
        TimeFormat timeFormat = new TimeFormat();
        assertThrows(IllegalArgumentException.class, () -> timeFormat.generateOutput("01052000, \"xxyyzzzz\""));
    }
}
