package io.metadew.iesi.script.execution.instruction.data.time;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TimeTravelTest {

    @Test
    void generateOutputHour() {
        TimeTravel TimeTravel = new TimeTravel();
        assertEquals("2000-05-02 22:12:12.121", TimeTravel.generateOutput("2000-05-02 12:12:12.121, \"hour\", 10"));
    }

    @Test
    void generateOutputHourOverflow() {
        TimeTravel TimeTravel = new TimeTravel();
        assertEquals("2000-05-03 12:12:12.121", TimeTravel.generateOutput("2000-05-02 12:12:12.121, \"hour\", 24"));
    }

    @Test
    void generateOutputMinute() {
        TimeTravel TimeTravel = new TimeTravel();
        assertEquals("2000-05-02 12:22:12.121", TimeTravel.generateOutput("2000-05-02 12:12:12.121, \"minute\", 10"));
    }

    @Test
    void generateOutputMinuteOverflow() {
        TimeTravel TimeTravel = new TimeTravel();
        assertEquals("2000-05-02 13:12:12.121", TimeTravel.generateOutput("2000-05-02 12:12:12.121, \"minute\", 60"));
    }

    @Test
    void generateOutputSecond() {
        TimeTravel TimeTravel = new TimeTravel();
        assertEquals("2000-05-02 12:12:22.121", TimeTravel.generateOutput("2000-05-02 12:12:12.121, \"second\", 10"));
    }

    @Test
    void generateOutputSecondOverflow() {
        TimeTravel TimeTravel = new TimeTravel();
        assertEquals("2000-05-02 12:13:12.121", TimeTravel.generateOutput("2000-05-02 12:12:12.121, \"second\", 60"));
    }

    @Test
    void generateOutputInputErrorTimeUnit() {
        TimeTravel TimeTravel = new TimeTravel();
        assertThrows(IllegalArgumentException.class, () -> TimeTravel.generateOutput("2000-05-02 12:12:12.121, \"test\", 10"));
    }

    @Test
    void generateOutputInputErrorStartTime() {
        TimeTravel TimeTravel = new TimeTravel();
        assertThrows(IllegalArgumentException.class, () -> TimeTravel.generateOutput("05-02-2000 12:12:12.121, \"hour\", 10"));
    }

    @Test
    void generateOutputInputErrorTimeQuantity() {
        TimeTravel TimeTravel = new TimeTravel();
        assertThrows(IllegalArgumentException.class, () -> TimeTravel.generateOutput("2000-05-02 12:12:12.121, \"hour\", a"));
    }
}
